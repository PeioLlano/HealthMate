package com.example.healthmate;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.healthmate.Ejercicio.AddEjercicioDialog;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity
    implements PantallaPrincipalFragment.ListenerPantallaPrincipalFragment, AddEjercicioDialog.AddEjercicioDialogListener {

    /* Atributos de la interfaz gráfica */
    private BottomNavigationView bnvOpciones;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        /*
        NavigationView navigationView = findViewById(R.id.nvSidebar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.shareFragment:
                        makeText(MainActivity.this, "Has pulsado el botón compartir", LENGTH_SHORT).show();
                        break;
                    case R.id.logoutFragment:
                        makeText(MainActivity.this, "Has pulsado el botón logout", LENGTH_SHORT).show();
                        break;
                }

                //drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            }
        });
        */
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

    @Override
    public void añadirEjercicio(String titulo, Date fecha, Double distancia, String tipo) {

    }
}
