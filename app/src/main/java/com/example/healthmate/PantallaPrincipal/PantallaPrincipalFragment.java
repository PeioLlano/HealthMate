package com.example.healthmate.PantallaPrincipal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.healthmate.MainActivity;
import com.example.healthmate.Modelo.Grafico;
import com.example.healthmate.NotificacionNoEjercicio.NoEjercicioNotificationHelper;
import com.example.healthmate.R;
import com.example.healthmate.Workers.SelectWorker;
import com.example.healthmate.pruebaGraficos.GraficoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PantallaPrincipalFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView rvGraficos;


    /* Otros atributos */
    private ListenerPantallaPrincipalFragment listenerPantallaPrincipalFragment;

    // Pedimos los registros de pasos que tenga el usuario en el último mes
    private final JSONArray[] jsonArray = {new JSONArray()};

    private ArrayList<Grafico> listaGraficos;
    private GraficoAdapter graficoAdapter;


    /*
     * Interfaz para que 'MainActivity' haga visible el 'BottomNavigationView' (tan sólo esta
     * actividad puede acceder a este elemento)
     */
    public interface ListenerPantallaPrincipalFragment {
        void mostrarBarraDeNavegacion();
        void ocultarBarraDeNavegacion();
    }

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
        return inflater.inflate(R.layout.fragment_pantalla_principal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listenerPantallaPrincipalFragment.mostrarBarraDeNavegacion();

        ((MainActivity) getActivity()).enableOptions();

        NoEjercicioNotificationHelper.scheduleNotification(requireContext());

        // Pedimos los permisos para notificaciones si es que no los tenemos
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)!=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new
                    String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        if (getArguments() != null) {
            String usuario = getArguments().getString("usuario");
            Log.d("PantallaPrincipalFragment", "USUARIO --> " + usuario);
        }

        listaGraficos = new ArrayList<>();

        // crearGrafico(view);

        // Obtenemos referencia de la lista de los gráficos
        rvGraficos = view.findViewById(R.id.rvGraficos);
        graficoAdapter = new GraficoAdapter(listaGraficos);

        rvGraficos.setAdapter(graficoAdapter);

        //cargarGrafico("Pasos");
        cargarGraficoMediciones("Presión arterial");
        cargarGraficoMediciones("Altura");
        cargarGraficoMediciones("Peso");
        cargarGraficoMediciones("IMC");
        cargarGraficoMediciones("Frecuencia cardíaca");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenerPantallaPrincipalFragment = (ListenerPantallaPrincipalFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("La clase " + context
                + " debe implementar ListenerPantallaPrincipalFragment");
        }
    }

    private void cargarGrafico(String tipo){
        // Worker para obtener los datos de los pasos del usuario logeado
        Data data = new Data.Builder()
                .putString("tabla", "Pasos")
                .putString("condicion", "nombre_usuario='"+((MainActivity) getActivity()).cargarLogeado()+"'" +
                        "AND MONTH(fecha)=5")
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(SelectWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(req.getId())
                .observe(requireActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String resultados = status.getOutputData().getString("resultados");
                        if (resultados.equals("null") || resultados.equals("")) resultados = null;
                        if (resultados != null) {
                            ArrayList<DataEntry> listaDecimal = new ArrayList<>();
                            try {
                                jsonArray[0] = new JSONArray(resultados);

                                for (int i = 0; i < jsonArray[0].length(); i++) {
                                    JSONObject obj = jsonArray[0].getJSONObject(i);
                                    Integer cantidadPasos = obj.getInt("cantidad_pasos");
                                    ValueDataEntry pasosDiaActual = new ValueDataEntry(i + 1, cantidadPasos);
                                    listaDecimal.add(pasosDiaActual);
                                }

                                listaGraficos.add(new Grafico(listaDecimal, tipo));
                                graficoAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);
    }

    private void cargarGraficoMediciones(String tipo){
        Calendar cal = Calendar.getInstance();
        // Worker para obtener los datos de los pasos del usuario logeado
        Data data = new Data.Builder()
                .putString("tabla", "Mediciones")
                .putString("condicion", "Usuario='"+((MainActivity) getActivity()).cargarLogeado()+"'" +
                        " AND MONTH(Fecha)=" + (cal.get(Calendar.MONTH)+1) +
                        " AND YEAR(Fecha)=" + (cal.get(Calendar.YEAR)) +
                        " AND Tipo='" + tipo + "' " +
                        "ORDER BY Fecha ASC")
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(SelectWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(req.getId())
                .observe(requireActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String resultados = status.getOutputData().getString("resultados");
                        if (resultados.equals("null") || resultados.equals("")) resultados = null;
                        if (resultados != null) {
                            ArrayList<DataEntry> listaDecimal = new ArrayList<>();
                            try {
                                jsonArray[0] = new JSONArray(resultados);

                                for (int i = 0; i < jsonArray[0].length(); i++) {
                                    JSONObject obj = jsonArray[0].getJSONObject(i);
                                    String medicion = obj.getString("Medicion");
                                    Float medicionec = Float.parseFloat(medicion.split(" ")[0]);
                                    ValueDataEntry medicionDiaActual = new ValueDataEntry(i + 1, medicionec);
                                    listaDecimal.add(medicionDiaActual);
                                }

                                listaGraficos.add(new Grafico(listaDecimal, tipo));

                                graficoAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);
    }
}