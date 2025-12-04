package com.example.juicy.Model;

public class PaypalCaptureRequest {
    private int id_cliente;
    private String orderId;

    public PaypalCaptureRequest(int id_cliente, String orderId) {
        this.id_cliente = id_cliente;
        this.orderId = orderId;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public String getOrderId() {
        return orderId;
    }
}
