package com.example.juicy.model;

public class GuardarMetodoPagoRequest {
    private int id_cliente;
    private String titular;
    private String num_tarjeta;
    private String fecha_expiracion; // "MM/AA" o "YYYY-MM"
    private String cvv;              // se envía pero NUNCA se lista
    private String cod_paypal;       // opcional

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void setNum_tarjeta(String num_tarjeta) {
        this.num_tarjeta = num_tarjeta;
    }

    public void setFecha_expiracion(String fecha_expiracion) {
        this.fecha_expiracion = fecha_expiracion;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setCod_paypal(String cod_paypal) {
        this.cod_paypal = cod_paypal;
    }
}
