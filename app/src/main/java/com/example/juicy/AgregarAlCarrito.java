package com.example.juicy;

import java.util.List;

public class AgregarAlCarrito {
    private String id_cliente;
    private String id_producto;
    private int cantidad;
    private List<String> agregados;
    private String presentacion;

    public AgregarAlCarrito(String id_cliente, String id_producto, int cantidad, List<String> agregados, String presentacion) {
        this.id_cliente = id_cliente;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.agregados = agregados;
        this.presentacion = presentacion;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public List<String> getAgregados() {
        return agregados;
    }

    public void setAgregados(List<String> agregados) {
        this.agregados = agregados;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }
}
