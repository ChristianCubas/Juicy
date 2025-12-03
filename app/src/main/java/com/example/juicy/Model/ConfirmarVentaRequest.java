package com.example.juicy.Model;

public class ConfirmarVentaRequest {
    private int id_cliente;
    private int id_venta;
    private int id_direccion;
    private int id_metodo_pago;

    public ConfirmarVentaRequest(int id_cliente, int id_venta, int id_direccion, int id_metodo_pago) {
        this.id_cliente = id_cliente;
        this.id_venta = id_venta;
        this.id_direccion = id_direccion;
        this.id_metodo_pago = id_metodo_pago;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_direccion() {
        return id_direccion;
    }

    public void setId_direccion(int id_direccion) {
        this.id_direccion = id_direccion;
    }

    public int getId_metodo_pago() {
        return id_metodo_pago;
    }

    public void setId_metodo_pago(int id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }
}

