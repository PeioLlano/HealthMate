package com.example.healthmate.Medicinas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.healthmate.Mediciones.AddMedicionDialog;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;
import com.example.healthmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class MedicinasActivity extends AppCompatActivity {

    /* Atributos de la interfaz gráfica */
    private BottomNavigationView bnvOpciones;
    /* Otros atributos */
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medicinas);

        bnvOpciones = findViewById(R.id.bnvOpciones);

        /* Configurar navegación con barra inferior */
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bnvOpciones, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_abajo_medicinas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    public String cargarLogeado() {
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String loged_user = preferences.getString("loged_user", "");

        return loged_user;
    }
}
