package com.example.healthmate.Workers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BuscarUbicaciones extends Worker {
    private Context context;

    public BuscarUbicaciones(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
            String urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location+"&radius="+radius+"&types="+types+"&key="+apikey;
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
                    OutputStreamWriter fichero = new OutputStreamWriter(context.openFileOutput("ubicaciones.txt",
                            Context.MODE_PRIVATE));
                    fichero.write(result);
                    fichero.close();
                } catch (IOException e){}

                try {
                    Log.d("resultados", result);

                    resultadosData = new Data.Builder()
                            .putBoolean("resultado", true)
                            .build();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return Result.success(resultadosData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
