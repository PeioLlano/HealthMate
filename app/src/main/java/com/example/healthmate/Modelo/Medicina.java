package com.example.healthmate.Modelo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class Medicina {

    private Integer codigo;
    private String nombre;
    private String hora;
    private ArrayList<String> dias;

    //Dejo abierto a meter mas cosas...

    public Medicina(Integer codigo, String nombre, String hora, ArrayList<String> dias) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.hora = hora;
        this.dias = dias;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHora() {
        return hora;
    }

    public ArrayList<String> getDias() {
        return dias;
    }

    public static String concatenateWithCommasAndAmpersand(ArrayList<String> lista) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) {
                if (i == lista.size() - 1) {
                    stringBuilder.append(" & ");
                } else {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(lista.get(i));
        }

        return stringBuilder.toString();
    }
}
