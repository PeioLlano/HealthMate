package com.example.healthmate.Modelo;

import com.anychart.scales.DateTime;

import java.util.Calendar;
import java.util.Date;

public class Ejercicio {

    private Integer codigo;
    private String titulo;
    private Date fecha;
    private Double distancia;
    private String tipo;

    //Dejo abierto a meter mas cosas...


    public Ejercicio(Integer codigo, String titulo, Date fecha, Double distancia, String tipo) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.fecha = fecha;
        this.distancia = distancia;
        this.tipo = tipo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public Integer setCodigo(Integer codigo) {
        return this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getDiaString() {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        String fecha = String.valueOf(c.get(Calendar.DATE))
                + '/' + String.valueOf(c.get(Calendar.MONTH))
                + '/' + String.valueOf(c.get(Calendar.YEAR));
        return fecha;
    }

    public Double getDistancia() {
        return distancia;
    }

    public String getTipo() {
        return tipo;
    }
}
