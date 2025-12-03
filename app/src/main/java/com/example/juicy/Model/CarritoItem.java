package com.example.juicy.Model;

public class CarritoItem {

    private int idDetalle;
    private String nombreProducto;
    private String personalizacionesJson;
    private int cantidad;
    private double precioTotal;

    public CarritoItem(int idDetalle, String nombreProducto, String personalizacionesJson, int cantidad, double precioTotal) {
        this.idDetalle = idDetalle;
        this.nombreProducto = nombreProducto;
        this.personalizacionesJson = personalizacionesJson;
        this.cantidad = cantidad;
        this.precioTotal = precioTotal;
    }

    public int getIdDetalle() { return idDetalle; }

    public String getNombreProducto() { return nombreProducto; }

    // Este getter procesará el JSON y devolverá texto bonito
    public String getDetallePersonalizacion() {
        return ProcesadorTexto.formatearPersonalizacion(personalizacionesJson);
    }

    public String getPersonalizacionesJson() { return personalizacionesJson; } // Getter crudo

    public int getCantidad() { return cantidad; }
    public double getPrecioTotal() { return precioTotal; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }
}