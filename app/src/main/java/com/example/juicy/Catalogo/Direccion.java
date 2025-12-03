package com.example.juicy.Catalogo;

import com.google.gson.annotations.SerializedName;

public class Direccion {
    @SerializedName("id_direccion")
    private int idDireccion;
    private String categoria;
    private String direccion;
    private String referencia;
    private String ciudad;
    @SerializedName("codigo_postal")
    private String codigoPostal;
    @SerializedName("es_principal")
    private int esPrincipalRaw;

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public boolean isEsPrincipal() {
        return esPrincipalRaw == 1;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipalRaw = esPrincipal ? 1 : 0;
    }

    public int getEsPrincipalRaw() {
        return esPrincipalRaw;
    }

    public void setEsPrincipalRaw(int esPrincipalRaw) {
        this.esPrincipalRaw = esPrincipalRaw;
    }
}
