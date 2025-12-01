package com.example.juicy.Model;

public class ValoracionProductoRequest {
    private int id_producto;
    private int puntuacion;
    private String comentario;

    public ValoracionProductoRequest(int id_producto, int puntuacion, String comentario) {
        this.id_producto = id_producto;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
