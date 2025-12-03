package com.example.juicy.Model;

import java.util.List;

public class Compra {
    private int id_venta;
    private String fecha;
    private String hora;
    private double total;

    // Aquí conectamos con la clase ProductoHistorial
    private List<ProductoHistorial> productos;

    public Compra() { }

    public int getId_venta() { return id_venta; }
    public void setId_venta(int id_venta) { this.id_venta = id_venta; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<ProductoHistorial> getProductos() { return productos; }
    public void setProductos(List<ProductoHistorial> productos) { this.productos = productos; }
}
