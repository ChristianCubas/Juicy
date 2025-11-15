// com/example/juicy/Model/CarritoItem.java
package com.example.juicy.Model;

public class CarritoItem {
    private String nombreProducto;
    private String tipo;
    private int cantidad;
    private double precioTotal;

    public CarritoItem(String nombreProducto, String tipo, int cantidad, double precioTotal) {
        this.nombreProducto = nombreProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precioTotal = precioTotal;
    }

    public String getNombreProducto() { return nombreProducto; }
    public String getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public double getPrecioTotal() { return precioTotal; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }
}
