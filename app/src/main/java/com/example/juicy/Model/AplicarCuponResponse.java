package com.example.juicy.Model;

public class AplicarCuponResponse {
    private int code;
    private String message;

    // Estos nombres deben coincidir EXACTAMENTE con los de tu JSON en Python
    // (api_aplicar_cupon devuelve: subtotal, descuento, total_final)
    private double subtotal;
    private double descuento;
    private double total_final;

    // Constructor vacío (requerido por Gson/Retrofit)
    public AplicarCuponResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotal_final() {
        return total_final;
    }

    public void setTotal_final(double total_final) {
        this.total_final = total_final;
    }
}
