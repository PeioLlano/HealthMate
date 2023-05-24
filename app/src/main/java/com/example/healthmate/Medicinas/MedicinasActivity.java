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
import android.widget.Toast;

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
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthmate.Mediciones.AddMedicionDialog;
import com.example.healthmate.Modelo.Medicina;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;
import com.example.healthmate.R;
import com.example.healthmate.Workers.DeleteWorker;
import com.example.healthmate.Workers.InsertWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MedicinasActivity extends AppCompatActivity implements ConsumoAdapter.CheckBoxListener{

    /* Atributos de la interfaz gráfica */
    private BottomNavigationView bnvOpciones;
    /* Otros atributos */
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Override
    public void añadirConsumo(Medicina m) {
        // Define el formato deseado para la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        // Formatea la fecha utilizando el formato definido
        String formattedDate = dateFormat.format(new Date());

        //Hacemos try de insertar el grupo para mostrar un toast en caso de que no se pueda insertar
        Data data = new Data.Builder()
                .putString("tabla", "Consumo")
                .putStringArray("keys", new String[]{"Codigo","Fecha"})
                .putStringArray("values", new String[]{String.valueOf(m.getCodigo()), formattedDate})
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(InsertWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(req);

        workManager.getWorkInfoByIdLiveData(req.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        Boolean resultados = status.getOutputData().getBoolean("resultado", false);
                        if(resultados) {

                        }
                        else {
                            Toast aviso = Toast.makeText(this, getResources().getString(R.string.error), Toast.LENGTH_SHORT);
                            aviso.show();
                        }
                    }});
    }

    @Override
    public void eliminarConsumo(Medicina m) {
        // Define el formato deseado para la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        // Formatea la fecha utilizando el formato definido
        String formattedDate = dateFormat.format(new Date());

        // Borramos el consumo seleccionado
        Data data = new Data.Builder()
                .putString("tabla", "Consumo")
                .putString("condicion", "Fecha = '" + formattedDate + "' AND Codigo = '" + m.getCodigo() + "'")
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(DeleteWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(req);

        workManager.getWorkInfoByIdLiveData(req.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        String resultados = status.getOutputData().getString("resultados");
                        if(resultados.equals("Ok")) {

                        }
                    }});
    }
}
