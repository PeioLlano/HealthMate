package com.example.healthmate.ChatBot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.Mapa.Mapa;
import com.example.healthmate.Mediciones.Mediciones;
import com.example.healthmate.Modelo.Mensaje;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipal;
import com.example.healthmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos el usuario necesario para obtener los demas datos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("usuario");
        }

        // Establecer la vista del layout "chat" en la actividad actual
        setContentView(R.layout.chat);

        // Obtener la vista BottomNavigationView del layout
        BottomNavigationView bnvOpciones = findViewById(R.id.bnvOpciones);

        // Seleccionar la pantalla en la que esta el usuario
        bnvOpciones.setSelectedItemId(R.id.chat);

        // Establecer un listener para la vista BottomNavigationView
        bnvOpciones.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtener el id del elemento seleccionado y mostrar un Toast con su nombre
                switch (item.getItemId()) {
                    case R.id.home:
                        //Toast.makeText(Chat.this, "home", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Chat.this, PantallaPrincipal.class);
                        intent.putExtra("usuario", username);
                        startActivity(intent);
                        Chat.this.finish();
                        break;
                    case R.id.sport:
                        Toast.makeText(Chat.this, "sport", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.map:
                        //Toast.makeText(PantallaPrincipal.this, "map", Toast.LENGTH_SHORT).show();
                        Intent intentMa = new Intent(Chat.this, Mapa.class);
                        intentMa.putExtra("usuario", username);
                        startActivity(intentMa);
                        Chat.this.finish();
                        break;
                    case R.id.measurements:
                        //Toast.makeText(PantallaPrincipal.this, "measure", Toast.LENGTH_SHORT).show();
                        Intent intentMe = new Intent(Chat.this, Mediciones.class);
                        intentMe.putExtra("usuario", username);
                        startActivity(intentMe);
                        Chat.this.finish();
                        break;
                    case R.id.chat:
                        //Toast.makeText(PantallaPrincipal.this, "chat", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        llVacia = findViewById(R.id.llVacia);
        llVacia.setVisibility(View.VISIBLE);

        // Inicialización de las vistas.
        rvMensajes = findViewById(R.id.rvMensajes); // Obtener la vista RecyclerView.
        fabMandar = findViewById(R.id.fabMandar); // Obtener el botón de envío de mensaje.
        etMensaje = findViewById(R.id.etMensaje); // Obtener el campo de texto de entrada de mensaje.

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
                    Toast.makeText(Chat.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Llamada al método para enviar el mensaje al bot y obtener una respuesta.
                sendMessage(etMensaje.getText().toString());

                // Establece el campo de texto a un valor vacío después del envío.
                etMensaje.setText("");
            }
        });

        // Inicialización del adaptador para la lista de mensajes.
        chatAdapter = new ChatAdapter(mensajeArrayList, this);

        // Establece un linear layout manager para el RecyclerView.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Chat.this, RecyclerView.VERTICAL, false);
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
