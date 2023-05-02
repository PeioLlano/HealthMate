package com.example.healthmate.Modelo;

public class Mensaje {

    private String mensaje;
    private String remitente;

    public Mensaje(String mensaje, String remitente) {
        this.mensaje = mensaje;
        this.remitente = remitente;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String message) {
        this.mensaje = message;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }
}
