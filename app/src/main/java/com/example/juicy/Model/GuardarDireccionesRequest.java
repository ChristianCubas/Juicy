package com.example.juicy.Model;

public class GuardarDireccionesRequest {
    private int idDireccion;
    private int idCliente;
    private String categoria;
    private String direccion;
    private String referencia;
    private String ciudad;
    private String codigoPostal;
    private boolean esPrincipal;

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
