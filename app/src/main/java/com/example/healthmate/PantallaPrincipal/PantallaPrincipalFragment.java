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
import com.example.healthmate.NotificacionNoEjercicio.NoEjercicioNotificationHelper;
import com.example.healthmate.R;
import com.example.healthmate.Workers.SelectWorker;
import com.example.healthmate.pruebaGraficos.GraficoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipalFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView rvGraficos;


    /* Otros atributos */
    private ListenerPantallaPrincipalFragment listenerPantallaPrincipalFragment;

    // Pedimos los registros de pasos que tenga el usuario en el último mes
    private final JSONArray[] jsonArray = {new JSONArray()};

    private List<DataEntry> listaPasos = new ArrayList<>();


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

        // crearGrafico(view);

        // Obtenemos referencia de la lista de los gráficos
        rvGraficos = view.findViewById(R.id.rvGraficos);

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
                        listaPasos = new ArrayList<>();
                        try {
                            jsonArray[0] = new JSONArray(resultados);

                            for (int i = 0; i < jsonArray[0].length(); i++) {
                                JSONObject obj = jsonArray[0].getJSONObject(i);
                                Integer cantidadPasos = obj.getInt("cantidad_pasos");
                                ValueDataEntry pasosDiaActual = new ValueDataEntry(i + 1, cantidadPasos);
                                listaPasos.add(pasosDiaActual);
                            }

                            rvGraficos.setAdapter(new GraficoAdapter(listaPasos));

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        WorkManager.getInstance(requireContext()).enqueue(req);
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

    private void crearGrafico(View view) {
        AnyChartView anyChartView = view.findViewById(R.id.grafico);
        /*ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        anyChartView.setProgressBar(progressBar);*/

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));
        seriesData.add(new CustomDataEntry("1989", 9.2, 11.8, 6.5));
        seriesData.add(new CustomDataEntry("1990", 10.1, 13.0, 12.5));
        seriesData.add(new CustomDataEntry("1991", 11.6, 13.9, 18.0));
        seriesData.add(new CustomDataEntry("1992", 16.4, 18.0, 21.0));
        seriesData.add(new CustomDataEntry("1993", 18.0, 23.3, 20.3));
        seriesData.add(new CustomDataEntry("1994", 13.2, 24.7, 19.2));
        seriesData.add(new CustomDataEntry("1995", 12.0, 18.0, 14.4));
        seriesData.add(new CustomDataEntry("1996", 3.2, 15.1, 9.2));
        seriesData.add(new CustomDataEntry("1997", 4.1, 11.3, 5.9));
        seriesData.add(new CustomDataEntry("1998", 6.3, 14.2, 5.2));
        seriesData.add(new CustomDataEntry("1999", 9.4, 13.7, 4.7));
        seriesData.add(new CustomDataEntry("2000", 11.5, 9.9, 4.2));
        seriesData.add(new CustomDataEntry("2001", 13.5, 12.1, 1.2));
        seriesData.add(new CustomDataEntry("2002", 14.8, 13.5, 5.4));
        seriesData.add(new CustomDataEntry("2003", 16.6, 15.1, 6.3));
        seriesData.add(new CustomDataEntry("2004", 18.1, 17.9, 8.9));
        seriesData.add(new CustomDataEntry("2005", 17.0, 18.9, 10.1));
        seriesData.add(new CustomDataEntry("2006", 16.6, 20.3, 11.5));
        seriesData.add(new CustomDataEntry("2007", 14.1, 20.7, 12.2));
        seriesData.add(new CustomDataEntry("2008", 15.7, 21.6, 10));
        seriesData.add(new CustomDataEntry("2009", 12.0, 22.5, 8.9));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Brandy");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("Whiskey");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("Tequila");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    private class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
    }

    /*private void crearGraficoDeBarras(View view) {
        AnyChartView graficoBarras = view.findViewById(R.id.grafico_barras);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        graficoBarras.setProgressBar(progressBar);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Rouge", 80540));
        data.add(new ValueDataEntry("Foundation", 94190));
        data.add(new ValueDataEntry("Mascara", 102610));
        data.add(new ValueDataEntry("Lip gloss", 110430));
        data.add(new ValueDataEntry("Lipstick", 128000));
        data.add(new ValueDataEntry("Nail polish", 143760));
        data.add(new ValueDataEntry("Eyebrow pencil", 170670));
        data.add(new ValueDataEntry("Eyeliner", 213210));
        data.add(new ValueDataEntry("Eyeshadows", 249980));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Top 10 Cosmetic Products by Revenue");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Product");
        cartesian.yAxis(0).title("Revenue");

        graficoBarras.setChart(cartesian);
    }*/
}