package com.example.juicy.Model;

public class PaypalCreateOrderRequest {
    private int id_cliente;
    private String currency;

    public PaypalCreateOrderRequest(int id_cliente, String currency) {
        this.id_cliente = id_cliente;
        this.currency = currency;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public String getCurrency() {
        return currency;
    }
}
