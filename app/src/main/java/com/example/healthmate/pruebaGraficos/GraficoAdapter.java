package com.example.healthmate.pruebaGraficos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.healthmate.Modelo.Grafico;
import com.example.healthmate.R;

import java.util.List;

public class GraficoAdapter extends RecyclerView.Adapter<GraficoViewHolder> {

    private List<Grafico> listaGraficos;


    public GraficoAdapter(List<Grafico> listaGraficos) {
        this.listaGraficos = listaGraficos;
    }

    @NonNull
    @Override
    public GraficoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View graficoItem = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.item_grafico_base, parent, false);
        GraficoViewHolder graficoViewHolder = new GraficoViewHolder(graficoItem);
        return graficoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GraficoViewHolder holder, int position) {
        Grafico graf = listaGraficos.get(position);
        //if (position == 0) {
        crearGraficoBarras(holder.graficoBase, graf.getGrafico(), graf.getTipo());
        //crearGraficoLineas(holder.graficoBase, graf.getGrafico(), graf.getTipo());
        //}
        /*else if (position == 1) {
            crearGraficoBarrasV2(holder.graficoBase);
        } else if (position == 2) {
            crearGraficoLineas(holder.graficoBase);
        }*/
    }

    // Gráfico para guardar los pasos de cada día del mes
    private void crearGraficoBarras(AnyChartView grafico, List<DataEntry> lista, String tipo) {
        Cartesian cartesian = AnyChart.column();

        Column column = cartesian.column(lista);

        column.tooltip()
                .titleFormat("Registro {%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: } " + tipo.toLowerCase());
        column.color("#7FFF00");

        cartesian.animation(true);
        cartesian.title(tipo + " este mes");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        //cartesian.xAxis(0).title("Día del mes");
        cartesian.yAxis(0).title(tipo);

        grafico.setChart(cartesian);
    }

    private void crearGraficoLineas(AnyChartView grafico, List<DataEntry> lista, String tipo) {
        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        Column column = cartesian.column(lista);

        column.tooltip()
                .titleFormat("Registro {%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: } " + tipo.toLowerCase());
        column.color("#7FFF00");

        cartesian.animation(true);
        cartesian.title(tipo + " este mes");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        //cartesian.xAxis(0).title("Día del mes");
        cartesian.yAxis(0).title(tipo);

        grafico.setChart(cartesian);
    }

    @Override
    public int getItemCount() {
        return listaGraficos.size();
    }
}
