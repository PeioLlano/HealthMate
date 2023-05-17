package com.example.healthmate.ChatBot;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.healthmate.Modelo.Mensaje;
import com.example.healthmate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {

    // Creación de variables para los widgets en el archivo xml.
    private RecyclerView rvMensajes; // Vista que muestra los mensajes del chat.
    private FloatingActionButton fabMandar; // Botón que envía los mensajes del usuario.
    private EditText etMensaje; // Campo de texto donde el usuario escribe sus mensajes.
    private final String USER_KEY = "user"; // Clave para identificar los mensajes del usuario.
    private final String BOT_KEY = "bot"; // Clave para identificar los mensajes del bot.

    // Creación de variables para el adaptador y la lista de mensajes.
    private ArrayList<Mensaje> mensajeArrayList; // Lista que contiene todos los mensajes del chat.
    private ChatAdapter chatAdapter; // Adaptador que muestra los mensajes en la vista de recycler.
    private String username;
    private LinearLayout llVacia;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llVacia = view.findViewById(R.id.llVacia);
        llVacia.setVisibility(View.VISIBLE);

        // Inicialización de las vistas
        rvMensajes = view.findViewById(R.id.rvMensajes);
        fabMandar = view.findViewById(R.id.fabMandar);
        etMensaje = view.findViewById(R.id.etMensaje);

        // Creación de una nueva lista de mensajes.
        mensajeArrayList = new ArrayList<>();

        // Agregando el listener del botón de envío de mensaje.
        fabMandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (llVacia.getVisibility() == View.VISIBLE) {
                    llVacia.setVisibility(View.INVISIBLE);
                }

                // Verificando si el mensaje ingresado por el usuario está vacío.
                if (etMensaje.getText().toString().isEmpty()) {
                    // Si el campo de texto está vacío, muestra un mensaje en la pantalla.
                    Toast.makeText(
                        requireContext(),
                        "Please enter your message..",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                // Llamada al método para enviar el mensaje al bot y obtener una respuesta.
                sendMessage(etMensaje.getText().toString());

                // Establece el campo de texto a un valor vacío después del envío.
                etMensaje.setText("");
            }
        });

        // Inicialización del adaptador para la lista de mensajes.
        chatAdapter = new ChatAdapter(mensajeArrayList, requireContext());

        // Establece un linear layout manager para el RecyclerView.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false);
        rvMensajes.setLayoutManager(linearLayoutManager);

        // Establece el adaptador para el RecyclerView.
        rvMensajes.setAdapter(chatAdapter);
    }

    private void sendMessage(String userMsg) {
        // La siguiente línea es para agregar el mensaje a nuestro ArrayList
        // que es ingresado por el usuario
        mensajeArrayList.add(new Mensaje(userMsg, USER_KEY));

        // Notificar al adaptador de que los datos han cambiado
        chatAdapter.notifyDataSetChanged();

        callAPI(userMsg);
    }


    void addResponse(String response){
        mensajeArrayList.remove(mensajeArrayList.size()-1);
        mensajeArrayList.add(new Mensaje(response, BOT_KEY));
        // Notificar al adaptador de que los datos han cambiado
        chatAdapter.notifyDataSetChanged();
        rvMensajes.smoothScrollToPosition(chatAdapter.getItemCount());
    }
    void callAPI(String question) {
        //okhttp
        mensajeArrayList.add(new Mensaje("Typing... ", BOT_KEY));
        chatAdapter.notifyDataSetChanged();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        addResponse("Failed to load response due to " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        // Los cambios en la interfaz gráfica hay que hacerlos en el hilo de la interfaz gráfica
                        // (estamos en una tarea de segundo plano)
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                addResponse(result.trim());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addResponse("Failed to load response due to " + response.body().toString());
                        }
                    });
                }
            }
        });

    }
}