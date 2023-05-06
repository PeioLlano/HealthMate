package com.example.healthmate;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
    implements PantallaPrincipalFragment.ListenerPantallaPrincipalFragment {

    /* Atributos de la interfaz gráfica */
    private BottomNavigationView bnvOpciones;


    /* Otros atributos */
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bnvOpciones = findViewById(R.id.bnvOpciones);

        /* Configurar navegación con barra inferior */
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bnvOpciones, navController);
    }

    /* Funciones para gestionar el menú inferior */
    @Override
    public void mostrarBarraDeNavegacion() {
        bnvOpciones.setVisibility(View.VISIBLE);
    }

    @Override
    public void ocultarBarraDeNavegacion() {
        bnvOpciones.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_abajo, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Obtener el id del elemento seleccionado y mostrar un Toast con su nombre
        int id = item.getItemId();
        switch (id) {
            case R.id.pantallaPrincipalFragment:
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sport:
                Toast.makeText(this, "sport", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mapaFragment:
                Toast.makeText(this, "map", Toast.LENGTH_SHORT).show();
                *//*Intent intentMa = new Intent(PantallaPrincipal.this, Mapa.class);
                intentMa.putExtra("usuario", username);
                startActivity(intentMa);
                PantallaPrincipal.this.finish();*//*
                break;
            case R.id.medicionesFragment:
                Toast.makeText(this, "measure", Toast.LENGTH_SHORT).show();
                *//*Intent intentMe = new Intent(PantallaPrincipal.this, Mediciones.class);
                intentMe.putExtra("usuario", username);
                startActivity(intentMe);
                PantallaPrincipal.this.finish();*//*
                break;
            case R.id.chatFragment:
                Toast.makeText(this, "chat", Toast.LENGTH_SHORT).show();
                *//*Intent intentC = new Intent(PantallaPrincipal.this, Chat.class);
                intentC.putExtra("usuario", username);
                startActivity(intentC);
                PantallaPrincipal.this.finish();*//*
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
            || super.onOptionsItemSelected(item);
    }
}
