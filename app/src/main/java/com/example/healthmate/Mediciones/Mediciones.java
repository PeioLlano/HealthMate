/*
package com.example.healthmate.Mediciones;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmate.Modelo.Medicion;
import com.example.healthmate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Date;

public class Mediciones extends AppCompatActivity {

    private String username;
    private ListView lvMediciones;
    private View llVacia;
    private MedicionAdapter pAdapter;
    private ArrayList<Medicion> mediciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos el usuario necesario para obtener los demas datos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("usuario");
        }

        // Establecer la vista del layout "mediciones" en la actividad actual
        setContentView(R.layout.mediciones);

        // Obtener la vista BottomNavigationView del layout
        BottomNavigationView bnvOpciones = findViewById(R.id.bnvOpciones);

        // Seleccionar la pantalla en la que esta el usuario
        bnvOpciones.setSelectedItemId(R.id.measurements);

        // Establecer un listener para la vista BottomNavigationView
        bnvOpciones.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtener el id del elemento seleccionado y mostrar un Toast con su nombre
                switch (item.getItemId()) {
                    case R.id.home:
                        //Toast.makeText(Mediciones.this, "home", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Mediciones.this, PantallaPrincipal.class);
                        intent.putExtra("usuario", username);
                        startActivity(intent);
                        Mediciones.this.finish();

                        break;
                    case R.id.sport:
                        Toast.makeText(Mediciones.this, "sport", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.map:
                        //Toast.makeText(Mediciones.this, "map", Toast.LENGTH_SHORT).show();
                        Intent intentMa = new Intent(Mediciones.this, Mapa.class);
                        intentMa.putExtra("usuario", username);
                        startActivity(intentMa);
                        Mediciones.this.finish();

                        break;
                    case R.id.measurements:
                        Toast.makeText(Mediciones.this, "measure", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.chat:
                        //Toast.makeText(Mediciones.this, "chat", Toast.LENGTH_SHORT).show();
                        Intent intentC = new Intent(Mediciones.this, Chat.class);
                        intentC.putExtra("usuario", username);
                        startActivity(intentC);
                        Mediciones.this.finish();
                        break;
                }
                return true;
            }
        });

        // Obtenemos la referencia al botón flotante de añadir
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        // Configuramos el listener para el botón de añadir
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Mediciones.this, "add", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtenemos la referencia al botón flotante de filtrar
        FloatingActionButton fabFilter = findViewById(R.id.fabFilter);

        // Configuramos el listener para el botón de filtrar
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Mediciones.this, "filter", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtenemos la referencia a la vista de lista de mediciones
        lvMediciones = (ListView) findViewById(R.id.lvMediciones);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = findViewById(R.id.llVacia);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        mediciones = new ArrayList<>();
        mediciones.add(new Medicion(1,"Medicion de peso",new Date("12/12/2022"), "88 kg", "Peso"));
        mediciones.add(new Medicion(2,"Medicion de altura",new Date("12/2/2023"), "189 cm", "Altura"));
        mediciones.add(new Medicion(3,"Medicion de IMC",new Date("19/2/2023"), "19.8", "IMC"));
        mediciones.add(new Medicion(4,"Medicion de FC",new Date("21/3/2023"), "190 ppm", "Frecuencia cardíaca"));
        mediciones.add(new Medicion(4,"Medicion de PA",new Date("1/3/2023"), "140 mmHg", "Presión arterial"));
        mediciones.add(new Medicion(4,"Medicion de NOS",new Date("23/3/2023"), "93 %", "Nivel de oxígeno en sangre"));

        // Creamos un adaptador para la lista de mediciones
        pAdapter = new MedicionAdapter(getApplicationContext(), mediciones);

        // Configuramos el adaptador para la vista de lista de mediciones
        lvMediciones.setAdapter(pAdapter);

        // Si el adaptador no contiene elementos, mostramos la vista de "lista vacía"
        if (pAdapter.getCount() == 0) {
            llVacia.setVisibility(View.VISIBLE);
            // lvMediciones.setVisibility(View.GONE);
        }

        // Creamos una variable para guardar la posición del elemento a borrar
        final Integer[] posAborrar = {-1};

        // Creamos un cuadro de diálogo para confirmar el borrado de una medición
        AlertDialog.Builder builderG = new AlertDialog.Builder(this);
        builderG.setCancelable(true);
        builderG.setTitle(getString(R.string.delete_measurement));
        builderG.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Borramos la medición seleccionada y notificamos al adaptador
                        borrarMedicion((Medicion) pAdapter.getItem(posAborrar[0]));
                        posAborrar[0] = -1;
                        pAdapter.notifyDataSetChanged();
                    }
                });
        builderG.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Creamos el cuadro de diálogo
        AlertDialog dialogBorrar = builderG.create();

        // Configuramos el listener para la vista de lista de mediciones al hacer clic prolongado
        lvMediciones.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // Guardamos la posición del elemento a borrar
                posAborrar[0] = pos;
                // Mostramos el cuadro de diálogo para confirmar el borrado
                dialogBorrar.show();
                return true;
            }
        });
    }

    // Método para borrar una medición (se implementa fuera del método onCreate)
    private void borrarMedicion (Medicion item){
        // Aquí se implementaría la lógica para borrar la medición
        mediciones.remove(item);
    }
}
*/
