package com.example.healthmate;

import static android.widget.Toast.makeText;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthmate.Medicinas.MedicinasActivity;
import com.example.healthmate.Mediciones.AddMedicionDialog;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;
import com.example.healthmate.Workers.BuscarHospitalCercano;
import com.example.healthmate.Workers.BuscarUbicaciones;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
    implements PantallaPrincipalFragment.ListenerPantallaPrincipalFragment,
        AddMedicionDialog.AddMedicionDialogListener,
        CambiarEstiloDialog.ListenerdelDialogoEstilo{

    /* Atributos de la interfaz gráfica */
    private BottomNavigationView bnvOpciones;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    /* Otros atributos */
    private NavController navController;
    private Button botonDesplegable;
    private Intent intentCall;
    private Integer requestCodeCall = 112;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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
                        break;

                    case R.id.change_style:
                        CambiarEstiloDialog dialogEst = new CambiarEstiloDialog();
                        dialogEst.show(getSupportFragmentManager(), "DialogoEstilo");
                        break;

                    case R.id.call:
                         dialog = ProgressDialog.show(MainActivity.this, "",getResources().getString(R.string.loading), true);
                        getDoctorMasCercano();
                        break;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestCodeCall) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realiza la llamada
                startActivity(intentCall);
            } else {
                // Permiso denegado, muestra un mensaje o realiza alguna acción alternativa
            }
        }
    }

    public void getDoctorMasCercano(){

        Data data = new Data.Builder()
                .putString("location", "43.263681,-2.951053")
                .putString("radius", "10000")
                .putString("apikey", "AIzaSyBT59rhxR2sQe9O28i_riW04jXP3SlI-5Q")
                .putString("types", "hospital")
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(BuscarHospitalCercano.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(req);

        workManager.getWorkInfoByIdLiveData(req.getId())
                .observe((LifecycleOwner) this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        Boolean resultados = status.getOutputData().getBoolean("resultado", false);

                        if (resultados) {
                            Log.d("telefono en main",status.getOutputData().getString("telefono"));

                            intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + status.getOutputData().getString("telefono")));

                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                // La aplicación tiene permisos para realizar llamadas telefónicas
                                startActivity(intentCall);
                            } else {
                                // La aplicación no tiene permisos, solicita al usuario que los conceda
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, requestCodeCall);
                            }

                            dialog.dismiss();

                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(this, getResources().getString(R.string.error_2), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void alElegirEstilo(int i) {
        int tiempoToast= Toast.LENGTH_SHORT;
        CharSequence[] opciones = {"Dark", getString(R.string.normal)};
        Toast avisoEstiloCambiado = Toast.makeText(this, getString(R.string.style_changed_to) + opciones[i], tiempoToast);
        avisoEstiloCambiado.show();

        guardarPreferenciaEstilo((String) opciones[i]);

        switch (i) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    public void guardarPreferenciaEstilo(String tema){
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tema", tema);
        editor.commit();
    }
}
