package com.example.healthmate.Modelo;

import java.util.Date;

public class Medicion {

    private Integer codigo;
    private String titulo;
    private Date fecha;
    private String medicion;
    private String tipo;

    //Dejo abierto a meter mas cosas...


    public Medicion(Integer codigo, String titulo, Date fecha, String medicion, String tipo) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.fecha = fecha;
        this.medicion = medicion;
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

    public String getMedicion() {
        return medicion;
    }

    public String getTipo() {
        return tipo;
    }
}
