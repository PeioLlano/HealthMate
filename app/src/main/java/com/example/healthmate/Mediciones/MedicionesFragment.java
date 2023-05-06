package com.example.healthmate.Mediciones;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.healthmate.Modelo.Medicion;
import com.example.healthmate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class MedicionesFragment extends Fragment {

    private String username;
    private ListView lvMediciones;
    private View llVacia;
    private MedicionAdapter pAdapter;
    private ArrayList<Medicion> mediciones;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_mediciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos la referencia al botón flotante de filtrar
        FloatingActionButton fabFilter = view.findViewById(R.id.fabFilter);

        // Configuramos el listener para el botón de filtrar
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "filter", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtenemos la referencia a la vista de lista de mediciones
        lvMediciones = view.findViewById(R.id.lvMediciones);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = view.findViewById(R.id.llVacia);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        mediciones = new ArrayList<>();
        mediciones.add(new Medicion(1,"Medicion de peso",new Date("12/12/2022"), "88 kg", "Peso"));
        mediciones.add(new Medicion(2,"Medicion de altura",new Date("12/2/2023"), "189 cm", "Altura"));
        mediciones.add(new Medicion(3,"Medicion de IMC",new Date("19/2/2023"), "19.8", "IMC"));
        mediciones.add(new Medicion(4,"Medicion de FC",new Date("21/3/2023"), "190 ppm", "Frecuencia cardíaca"));
        mediciones.add(new Medicion(4,"Medicion de PA",new Date("1/3/2023"), "140 mmHg", "Presión arterial"));
        mediciones.add(new Medicion(4,"Medicion de NOS",new Date("23/3/2023"), "93 %", "Nivel de oxígeno en sangre"));

        // Creamos un adaptador para la lista de mediciones
        pAdapter = new MedicionAdapter(requireContext(), mediciones);

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
        AlertDialog.Builder builderG = new AlertDialog.Builder(requireContext());
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