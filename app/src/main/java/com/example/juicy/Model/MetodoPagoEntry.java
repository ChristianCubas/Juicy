package com.example.juicy.Model;

public class MetodoPagoEntry {
    private int id_metodo_pago;
    private String titular;
    private String num_tarjeta;
    private String fecha_expiracion;
    private String cod_paypal;
    private int estado;

    public int getId_metodo_pago() {
        return id_metodo_pago;
    }

    public String getTitular() {
        return titular;
    }

    public String getNum_tarjeta() {
        return num_tarjeta;
    }

    public String getFecha_expiracion() {
        return fecha_expiracion;
    }

    public String getCod_paypal() {
        return cod_paypal;
    }

    public int getEstado() {
        return estado;
    }

    // Máscara para mostrar la tarjeta
    public String getNum_tarjeta_mask() {
        if (num_tarjeta == null || num_tarjeta.length() < 4) return "****";
        String last4 = num_tarjeta.substring(num_tarjeta.length() - 4);
        return "**** **** **** " + last4;
    }
}
