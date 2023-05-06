package com.example.healthmate.Mapa;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.healthmate.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    /* Atributos de la interfaz gráfica */
    private GoogleMap mapa;

    /* Otros atributos */

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
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos el fragmento del mapa mediante su ID y lo asignamos a una variable
        SupportMapFragment fragmentoMapa = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fcvMapa);

        // Solicitamos de manera asíncrona el mapa a través del fragmento
        fragmentoMapa.getMapAsync(this);

        // Creamos una lista de posibles búsquedas y la populamos con algunos valores
        ArrayList<String> posiblesBusquedas = new ArrayList<>();
        posiblesBusquedas.add("Farmacias");
        posiblesBusquedas.add("Ambulatorios");
        posiblesBusquedas.add("Hospitales");
        posiblesBusquedas.add("Nutricionistas");

        // Creamos un adaptador de array para el spinner con la lista de posibles búsquedas
        ArrayAdapter adpSpinner = new ArrayAdapter(
            requireContext(),
            R.layout.spinner_texto,
            posiblesBusquedas
        );
        adpSpinner.setDropDownViewResource(R.layout.spinner_drop);

        // Obtenemos el spinner y le asignamos el adaptador de array
        Spinner sBusqueda = view.findViewById(R.id.sBusqueda);
        sBusqueda.setAdapter(adpSpinner);

        // Obtenemos el botón de búsqueda y le asignamos un listener para cuando se haga click
        FloatingActionButton fabSearch = view.findViewById(R.id.fabSearch);
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "buscar", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtenemos el botón de localización y le asignamos un listener para cuando se haga click
        FloatingActionButton fabLocate = view.findViewById(R.id.fabLocate);
        fabLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "localizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * En esta función, una vez que el mapa está listo para ser mostrado, se crea una instancia
     * para poder ser utilizado a lo largo del código.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Asignamos el mapa obtenido a la variable 'elmapa'
        mapa = googleMap;

        // Establecemos el tipo de mapa a mostrar (en este caso, MAP_TYPE_NORMAL)
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}