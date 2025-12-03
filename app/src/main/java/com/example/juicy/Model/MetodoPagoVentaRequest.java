package com.example.juicy.Model;

import com.example.juicy.Model.ApiMetodosPagoRequest;

public class MetodoPagoVentaRequest {
    private int id_cliente;
    private int id_metodo_pago;
    private ApiMetodosPagoRequest.NuevoMetodo nuevo_metodo;

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setId_metodo_pago(int id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }

    public void setNuevo_metodo(ApiMetodosPagoRequest.NuevoMetodo nuevo_metodo) {
        this.nuevo_metodo = nuevo_metodo;
    }

}
