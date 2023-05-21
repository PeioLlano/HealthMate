package com.example.healthmate;

import static android.widget.Toast.makeText;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.os.LocaleListCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.healthmate.Ejercicio.AddEjercicioDialog;
import com.example.healthmate.Medicinas.MedicinasActivity;
import com.example.healthmate.Mediciones.AddMedicionDialog;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity
    implements PantallaPrincipalFragment.ListenerPantallaPrincipalFragment,
        AddMedicionDialog.AddMedicionDialogListener {

    /* Atributos de la interfaz gráfica */
    private BottomNavigationView bnvOpciones;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    /* Otros atributos */
    private NavController navController;
    private Button botonDesplegable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bnvOpciones = findViewById(R.id.bnvOpciones);

        drawerLayout = findViewById(R.id.my_drawer_layout);

        botonDesplegable = findViewById(R.id.botonDesplegable);

        botonDesplegable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        /* Configurar navegación con barra inferior */
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bnvOpciones, navController);

        NavigationView navigationView = findViewById(R.id.nvSidebar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.shareFragment:
                        String appInfo = getString(R.string.app_info);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, appInfo);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_healthmate));
                        startActivity(shareIntent);
                        break;

                    case R.id.about:
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("HealthMate");
                        alertDialog.setMessage(getString(R.string.about_text));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.show();
                        break;

                    case R.id.logoutFragment:
                        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("loged_user", "");
                        editor.commit();

                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).popBackStack(R.id.loginFragment, false);

                        break;

                    case R.id.medicines:
                        //makeText(MainActivity.this, "TO DO", LENGTH_SHORT).show();
                        //Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(R.id.medicinasFragment);
                        Intent intent = new Intent(MainActivity.this, MedicinasActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.change_language:
                        CambiarIdiomaDialog cambiarIdiomaDialog = new CambiarIdiomaDialog();
                        cambiarIdiomaDialog.show(getSupportFragmentManager(), "DialogoCambiarIdioma");
                }

                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;

            }
        });

        View headerView = navigationView.getHeaderView(0); // Obtiene la vista del encabezado del NavigationView
        TextView tvUsername = headerView.findViewById(R.id.tvUsername);
        tvUsername.setText(cargarLogeado());
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

    public void disableOptions() {
        botonDesplegable.setVisibility(View.INVISIBLE);
        botonDesplegable.setClickable(false);
        bnvOpciones.setVisibility(View.GONE);
    }

    public void enableOptions() {
        botonDesplegable.setVisibility(View.VISIBLE);
        botonDesplegable.setClickable(true);
        bnvOpciones.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_abajo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
            || super.onOptionsItemSelected(item);
    }

    @Override
    public void añadirMedicion(String titulo, Date fecha, String medicion, String tipo) {

    }

    public String cargarLogeado() {
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String loged_user = preferences.getString("loged_user", "");

        return loged_user;
    }
}
