package com.example.healthmate.Mapa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthmate.R;
import com.example.healthmate.Workers.BuscarUbicaciones;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapaFragment extends Fragment implements OnMapReadyCallback {

    /* Atributos de la interfaz gráfica */
    private GoogleMap mapa;
    private PlacesClient placesClient;

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

        pedirPermisosUbicacion();

        // Obtenemos el fragmento del mapa mediante su ID y lo asignamos a una variable
        SupportMapFragment fragmentoMapa = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fcvMapa);

        // Solicitamos de manera asíncrona el mapa a través del fragmento
        fragmentoMapa.getMapAsync(this);

        // Creamos una lista de posibles búsquedas y la populamos con algunos valores
        ArrayList<String> posiblesBusquedas = new ArrayList<>();
        posiblesBusquedas.add(getString(R.string.pharmacies));
        //posiblesBusquedas.add("Ambulatorios");
        posiblesBusquedas.add(getString(R.string.hospitals));
        //posiblesBusquedas.add("Nutricionistas");

        ArrayList<String> posiblesBusquedasIngles = new ArrayList<>();
        posiblesBusquedasIngles.add("pharmacy");
        //posiblesBusquedasIngles.add("doctor");
        posiblesBusquedasIngles.add("hospital");
        //posiblesBusquedasIngles.add("health");

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
                buscar(posiblesBusquedasIngles.get(sBusqueda.getSelectedItemPosition()), "43.263681,-2.951053", "500");
            }
        });

        // Obtenemos el botón de localización y le asignamos un listener para cuando se haga click
        FloatingActionButton fabLocate = view.findViewById(R.id.fabLocate);
        fabLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerUbicacionActual();
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

    private void buscar(String tipo, String ubicacion, String radio) {
        final Double[] totalLon = {0.0d}; // variable para guardar la suma de longitudes
        final Double[] totalLat = {0.0d}; // variable para guardar la suma de latitudes
        final Integer[] cantPos = {0}; // variable para guardar la cantidad de posiciones de los gastos del grupo

        Data data = new Data.Builder()
                .putString("location", ubicacion)
                .putString("radius", radio)
                .putString("apikey", "AIzaSyBT59rhxR2sQe9O28i_riW04jXP3SlI-5Q")
                .putString("types", tipo)
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(BuscarUbicaciones.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(requireContext());
        workManager.enqueue(req);

        workManager.getWorkInfoByIdLiveData(req.getId())
                .observe((LifecycleOwner) requireContext(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        Boolean resultados = status.getOutputData().getBoolean("resultado", false);

                        if (resultados) {
                            String resultadosStr = "";
                            try {
                                BufferedReader ficherointerno = new BufferedReader(new InputStreamReader(
                                        requireContext().openFileInput("ubicaciones.txt")));
                                resultadosStr = ficherointerno.readLine();
                                ficherointerno.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            System.out.println(resultadosStr);

                            try {
                                mapa.clear();

                                JSONObject jsonObject = new JSONObject(resultadosStr);

                                JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));

                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject obJSON = jsonArray.getJSONObject(j);

                                    String geometry = obJSON.getString("geometry");
                                    JSONObject jsonGeometry = new JSONObject(geometry);
                                    String location = jsonGeometry.getString("location");
                                    JSONObject jsonLocation = new JSONObject(location);
                                    Double lat = jsonLocation.getDouble("lat");
                                    Double lng = jsonLocation.getDouble("lng");

                                    String name = obJSON.getString("name");

                                    System.out.println("-------" + j + "--------");
                                    System.out.println("name: " + name);
                                    System.out.println("lat: " + lat);
                                    System.out.println("lng: " + lng);

                                    // se agrega un marcador en el mapa en la posición del
                                    // resultado de búsqueda
                                    mapa.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .title(name));

                                    totalLon[0] += lng; // se suma la longitud del resultado actual
                                    totalLat[0] += lat; // se suma la latitud del resultado actual
                                    cantPos[0] += 1; // se aumenta el contador de posiciones
                                }

                                if (cantPos[0] != 0) {
                                    CameraPosition Poscam = new CameraPosition.Builder()
                                            .target(new LatLng(totalLat[0] / cantPos[0], totalLon[0] / cantPos[0]))
                                            .zoom(16f)
                                            .build();
                                    CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
                                    mapa.animateCamera(otravista);
                                } else {
                                    CameraPosition Poscam = new CameraPosition.Builder()
                                            .target(new LatLng(43.9785280, 15.3833720))
                                            .zoom(15.5f)
                                            .build();
                                    CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
                                    mapa.animateCamera(otravista);
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
    }

    // PONER LUEGO DONDE TOQUE
    private void pedirPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        }
    }

    private void obtenerUbicacionActual() {
        FusedLocationProviderClient proveedordelocalizacion =
                LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        proveedordelocalizacion.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("MapaFragment", "LATITUDE: " + location.getLatitude());
                            Log.d("MapaFragment", "LONGITUDE: " + location.getLongitude());
                            CameraPosition Poscam = new CameraPosition.Builder()
                                    .target(
                                        new LatLng(location.getLatitude(), location.getLongitude())
                                    )
                                    .zoom(16f)
                                    .build();
                            CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
                            mapa.animateCamera(otravista);
                        } else {
                            Log.d("MapaFragment", "LATITUDE: desconocida" );
                            Log.d("MapaFragment", "LONGITUDE: desconocida");
                            Log.d("MapaFragment", "success error");
                            Toast.makeText(
                                requireContext(),
                                R.string.cant_obtain_location,
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }).addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapaFragment", "failure error");
                        Toast.makeText(
                            requireContext(),
                            R.string.cant_obtain_location,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}