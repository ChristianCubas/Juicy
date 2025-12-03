package com.example.juicy.Model;

public class AplicarCuponRequest {
    private int id_cliente;
    private String codigo_cupon;

    public AplicarCuponRequest(int idCliente, String codigo) {
        this.id_cliente = idCliente;
        this.codigo_cupon = codigo;

    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getCodigo_cupon() {
        return codigo_cupon;
    }

    public void setCodigo_cupon(String codigo_cupon) {
        this.codigo_cupon = codigo_cupon;
    }
}
