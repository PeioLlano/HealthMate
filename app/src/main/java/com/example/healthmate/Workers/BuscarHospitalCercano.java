package com.example.healthmate.Workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.healthmate.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BuscarHospitalCercano extends Worker {
    private Context context;

    public BuscarHospitalCercano(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        String location = getInputData().getString("location");
        String radius = getInputData().getString("radius");
        String apikey = getInputData().getString("apikey");
        String types = getInputData().getString("types");

        try {
            String urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location + "&radius=" + radius + "&types=" + types + "&key=" + apikey;
            Log.d("req", urlStr);
            HttpURLConnection urlConnection = null;
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);


            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.close();

            int statusCode = urlConnection.getResponseCode();
            String line, result = "";

            Log.d("statusCode", String.valueOf(statusCode));

            Data resultadosData = null;

            resultadosData = new Data.Builder()
                    .putBoolean("resultado", false)
                    .build();

            if (statusCode == 200) {

                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));

                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject obJSON = jsonArray.getJSONObject(j);

                        String name = obJSON.getString("name");

                        System.out.println("-------" + j + "--------");
                        System.out.println("name: " + name);

                        String telefono = "";

                        OkHttpClient client = new OkHttpClient();

                        // Reemplaza los espacios en blanco en el nombre del negocio y la ubicación por el símbolo "+"
                        String busqueda = name.replace(" ", "+") + "+numero+de+telefono";

                        // URL de búsqueda en Google
                        String urlStr2 = "https://www.google.com/search?q=" + busqueda;

                        Log.d("req", urlStr2);

                        Request request = new Request.Builder()
                                .url(urlStr2)
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful()) {
                                String responseBody = response.body().string();

                                Log.d("responseBody", responseBody);

                                // Analizar el HTML de la respuesta para extraer el número de teléfono
                                // Aquí puedes utilizar una biblioteca de análisis HTML como Jsoup

                                // Ejemplo: Extraer el número de teléfono de la respuesta utilizando expresiones regulares
                                String regex = "\\+\\d{2} \\d{3} \\d{2} \\d{2} \\d{2}|\\+\\d{2} \\d{9}|\\+\\d{2} \\d{3} \\d{3} \\d{3}";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(responseBody);

                                if (matcher.find()) {
                                    // Se encontró un número de teléfono en la respuesta
                                    // Formar el número de teléfono completo
                                    telefono = matcher.group();
                                }
                            }
                        } catch (IOException e) {
                            Log.e("error", "Error al realizar la solicitud HTTP: " + e.getMessage());
                        }

                        Log.d("telefono", telefono);

                        if (telefono != "") {
                            resultadosData = new Data.Builder()
                                    .putBoolean("resultado", true)
                                    .putString("telefono", telefono)
                                    .build();
                            return Result.success(resultadosData);
                        }

                    }

                    return Result.success(resultadosData);

                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.failure();
                }
            }
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.failure();
    }
}
