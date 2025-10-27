package com.example.juicy;

public class MetodoPagoEntry {
    private int id_metodo_pago;
    private String titular;
    private String num_tarjeta_mask;   // viene enmascarado desde la API
    private String fecha_expiracion;   // "MM/AA" o "YYYY-MM"
    private String cod_paypal;         // puede ser null
    private int estado;

    public int getId_metodo_pago() {
        return id_metodo_pago;
    }

    public String getTitular() {
        return titular;
    }

    public String getNum_tarjeta_mask() {
        return num_tarjeta_mask;
    }

    public String getCod_paypal() {
        return cod_paypal;
    }

    public String getFecha_expiracion() {
        return fecha_expiracion;
    }

    public int getEstado() {
        return estado;
    }
}
