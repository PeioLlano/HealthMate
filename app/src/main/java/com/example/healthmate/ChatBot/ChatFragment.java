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

import java.util.ArrayList;

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

        // Ejemplo de respuesta del bot
        String botResponse = "Ejemplo de respuesta";


        // Agregar un nuevo mensaje al ArrayList para la respuesta del bot
        mensajeArrayList.add(new Mensaje(botResponse, BOT_KEY));

        // Notificar al adaptador de que los datos han cambiado
        chatAdapter.notifyDataSetChanged();
    }

}