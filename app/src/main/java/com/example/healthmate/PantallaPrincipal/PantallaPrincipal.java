package com.example.healthmate.PantallaPrincipal;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PantallaPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer la vista del layout "pantalla_principal" en la actividad actual
        setContentView(R.layout.pantalla_principal);

        // Obtener la vista BottomNavigationView del layout
        BottomNavigationView bnvOpciones = findViewById(R.id.bnvOpciones);

        // Establecer un listener para la vista BottomNavigationView
        bnvOpciones.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtener el id del elemento seleccionado y mostrar un Toast con su nombre
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(PantallaPrincipal.this, "home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sport:
                        Toast.makeText(PantallaPrincipal.this, "sport", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.map:
                        Toast.makeText(PantallaPrincipal.this, "map", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.measurements:
                        Toast.makeText(PantallaPrincipal.this, "measure", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.chat:
                        Toast.makeText(PantallaPrincipal.this, "chat", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}
