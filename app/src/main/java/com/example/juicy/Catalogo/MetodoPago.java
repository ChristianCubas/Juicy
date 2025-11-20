package com.example.juicy.Catalogo;

import java.util.List;

public class MetodoPago {

    private Integer idMetodoPago;
    private String titular;
    private String numTarjeta;
    private String fechaExpiracion;
    private String codPaypal;
    private boolean estado;
    private boolean selected = false; // Asegúrate de poder manejar la selección


    // Getters and Setters
    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getCodPaypal() {
        return codPaypal;
    }

    public void setCodPaypal(String codPaypal) {
        this.codPaypal = codPaypal;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    private List<MetodoPago> metodoPagos;

    public List<MetodoPago> getMetodoPagos() {
        return metodoPagos;
    }
    public void setMetodoPagos(List<Direccion> direcciones) {
        this.metodoPagos = metodoPagos;
    }
}

