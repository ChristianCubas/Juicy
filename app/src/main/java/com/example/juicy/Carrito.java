package com.example.juicy;

public class Carrito {
    private int id_venta;
    private int id_producto;
    private int nro_pedido;
    private int cantidad;
    private float subtotal;

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getNro_pedido() {
        return nro_pedido;
    }

    public void setNro_pedido(int nro_pedido) {
        this.nro_pedido = nro_pedido;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }
}
