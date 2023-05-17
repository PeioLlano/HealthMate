package com.example.healthmate;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.healthmate.Ejercicio.AddEjercicioDialog;
import com.example.healthmate.Login.LoginFragment;
import com.example.healthmate.Login.LoginFragmentDirections;
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
                        String appInfo = "¡Descubre HealthMate, tu compañero de salud personalizado!\n\n" +
                                "HealthMate es una aplicación diseñada para ayudarte a mejorar tu bienestar y llevar un estilo de vida saludable. Con una variedad de características y funciones, te ofrece las herramientas necesarias para gestionar tus mediciones de ejercicio, controlar tus hábitos alimenticios, hacer un seguimiento de tu progreso y mucho más.\n\n" +
                                "Principales características de HealthMate:\n" +
                                "- Registra tus ejercicios: Mantén un registro de tus actividades físicas, como correr, nadar, andar en bicicleta, etc., para un seguimiento efectivo de tu rutina de ejercicios.\n" +
                                "- Gráficos y estadísticas: Visualiza tu progreso a través de gráficos y estadísticas detalladas, lo que te permitirá evaluar tus avances y establecer metas realistas.\n" +
                                "- Recordatorios y alarmas: Configura recordatorios y alarmas personalizadas para mantenerte motivado y no olvidar tus actividades y hábitos saludables.\n\n" +
                                "Descarga HealthMate hoy mismo y comienza a cuidar de tu salud y bienestar de manera fácil y efectiva.\n\n" +
                                "¡Mejora tu vida con HealthMate!";

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, appInfo);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, "Compartir HealthMate");
                        startActivity(shareIntent);
                        break;

                    case R.id.about:
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("HealthMate");
                        alertDialog.setMessage("\"¡Bienvenido a nuestra aplicación!\n" +
                                "\n" +
                                "En HealthMate, nos dedicamos a proporcionarte una experiencia única y enriquecedora para tu bienestar y salud. Nuestra misión es ayudarte a llevar un estilo de vida saludable, brindándote herramientas y recursos que te permitan alcanzar tus metas de forma fácil y divertida.\n" +
                                "\n" +
                                "¡Gracias por elegirnos!\n" +
                                "\n" +
                                "El equipo de HealthMate\"");
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

                        // Ir a login
                        Fragment newFragment = new LoginFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        transaction.replace(R.id.nav_host_fragment, newFragment);

                        transaction.commit();

                        break;

                    case R.id.medicines:
                        makeText(MainActivity.this, "TO DO", LENGTH_SHORT).show();

                        break;
                }

                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;

            }
        });

        TextView tvUsername = navigationView.findViewById(R.id.tvUsername);
        //tvUsername.setText(cargarLogeado());
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
    }

    public void enableOptions() {
        botonDesplegable.setVisibility(View.VISIBLE);
        botonDesplegable.setClickable(true);
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
