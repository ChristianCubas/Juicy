package com.example.juicy.Model;

public class PaypalComprobanteRequest {
    private String correo;  // Correo de PayPal
    private double totalVenta;  // Total de la compra

    // Constructor
    public PaypalComprobanteRequest(String correo, double totalVenta) {
        this.correo = correo;
        this.totalVenta = totalVenta;
    }

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
}
