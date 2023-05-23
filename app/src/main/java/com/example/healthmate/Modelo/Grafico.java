package com.example.healthmate.Modelo;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import java.util.List;

public class Grafico {

    private List<DataEntry> grafico;
    private String tipo;

    public Grafico(List<DataEntry> grafico, String tipo) {
        this.grafico = grafico;
        this.tipo = tipo;
    }

    public List<DataEntry> getGrafico() {
        return grafico;
    }

    public String getTipo() {
        return tipo;
    }

    public void setGrafico(List<DataEntry> grafico) {
        this.grafico = grafico;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
