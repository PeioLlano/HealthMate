package com.example.healthmate.Mapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.healthmate.Mediciones.Mediciones;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipal;
import com.example.healthmate.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap elmapa;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos el usuario necesario para obtener los demas datos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("usuario");
        }

        // Establecer la vista del layout "mediciones" en la actividad actual
        setContentView(R.layout.mapa);

        // Obtener la vista BottomNavigationView del layout
        BottomNavigationView bnvOpciones = findViewById(R.id.bnvOpciones);

        // Seleccionar la pantalla en la que esta el usuario
        bnvOpciones.setSelectedItemId(R.id.map);

        // Establecer un listener para la vista BottomNavigationView
        bnvOpciones.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtener el id del elemento seleccionado y mostrar un Toast con su nombre
                switch (item.getItemId()) {
                    case R.id.home:
                        //Toast.makeText(Mediciones.this, "home", Toast.LENGTH_SHORT).show();
                        Intent intentH = new Intent(Mapa.this, PantallaPrincipal.class);
                        intentH.putExtra("usuario", username);
                        startActivity(intentH);
                        Mapa.this.finish();

                        break;
                    case R.id.sport:
                        Toast.makeText(Mapa.this, "sport", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.map:
                        Toast.makeText(Mapa.this, "map", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.measurements:
                        //Toast.makeText(Mapa.this, "measure", Toast.LENGTH_SHORT).show();
                        Intent intentMe = new Intent(Mapa.this, Mediciones.class);
                        intentMe.putExtra("usuario", username);
                        startActivity(intentMe);
                        Mapa.this.finish();

                        break;
                    case R.id.chat:
                        Toast.makeText(Mapa.this, "chat", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        // Obtenemos el fragmento del mapa mediante su ID y lo asignamos a una variable
        SupportMapFragment elfragmento = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fcvMapa);

        // Solicitamos de manera asíncrona el mapa a través del fragmento
        elfragmento.getMapAsync(this);

        // Creamos una lista de posibles búsquedas y la populamos con algunos valores
        ArrayList<String> posiblesBusquedas = new ArrayList<>();
        posiblesBusquedas.add("Farmacias");
        posiblesBusquedas.add("Ambulatorios");
        posiblesBusquedas.add("Hospitales");
        posiblesBusquedas.add("Nutricionistas");

        // Creamos un adaptador de array para el spinner con la lista de posibles búsquedas
        ArrayAdapter adpSpinner = new ArrayAdapter(this, R.layout.spinner_texto, posiblesBusquedas);
        adpSpinner.setDropDownViewResource(R.layout.spinner_drop);

        // Obtenemos el spinner y le asignamos el adaptador de array
        Spinner sBusqueda = findViewById(R.id.sBusqueda);
        sBusqueda.setAdapter(adpSpinner);

        // Obtenemos el botón de búsqueda y le asignamos un listener para cuando se haga click
        FloatingActionButton fabSearch = findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Mapa.this, "buscar", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtenemos el botón de localización y le asignamos un listener para cuando se haga click
        FloatingActionButton fabLocate = findViewById(R.id.fabLocate);
        fabLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Mapa.this, "localizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método que se ejecuta cuando el mapa está listo para ser usado
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Asignamos el mapa obtenido a la variable 'elmapa'
        elmapa = googleMap;
        // Establecemos el tipo de mapa a mostrar (en este caso, MAP_TYPE_NORMAL)
        elmapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}