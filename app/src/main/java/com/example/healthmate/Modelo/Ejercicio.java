package com.example.healthmate.Modelo;

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

    public String getTitulo() {
        return titulo;
    }

    public Date getFecha() {
        return fecha;
    }

    public Double getDistancia() {
        return distancia;
    }

    public String getTipo() {
        return tipo;
    }
}
