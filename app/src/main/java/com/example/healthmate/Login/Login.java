package com.example.healthmate.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.healthmate.PantallaPrincipal.PantallaPrincipal;
import com.example.healthmate.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer la vista del layout "login" en la actividad actual
        setContentView(R.layout.login);

        // Buscar las vistas "EditText" con los identificadores "eUsername" y "ePassword" en el layout
        EditText eUsername = findViewById(R.id.eUsername);
        EditText ePassword = findViewById(R.id.ePassword);

        // Buscar la vista "Button" con el identificador "bSignIn" en el layout y establecer un listener de clic
        Button bSingIn = findViewById(R.id.bSignIn);
        bSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Llamar al método "comprobarLogeo" con los valores ingresados en las vistas "EditText"
                comprobarLogeo(eUsername.getText().toString(), ePassword.getText().toString());

            }
        });
    }

    // Método privado para comprobar el inicio de sesión del usuario
    private void comprobarLogeo(String username, String password) {
        // Implementar la lógica para comprobar el inicio de sesión del usuario

        Intent intent = new Intent(Login.this, PantallaPrincipal.class);
        intent.putExtra("usuario", username);
        startActivity(intent);
        Login.this.finish();
    }
}