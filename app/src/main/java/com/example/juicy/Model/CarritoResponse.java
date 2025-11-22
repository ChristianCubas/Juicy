package com.example.juicy.Model;

import com.google.gson.annotations.SerializedName;

public class CarritoResponse {

    @SerializedName("id_cliente")
    private int idCliente;
    @SerializedName("id_venta")
    private int idVenta;
    @SerializedName("total_general")
    private double totalGeneral;

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public double getTotalGeneral() {
        return totalGeneral;
    }
}

