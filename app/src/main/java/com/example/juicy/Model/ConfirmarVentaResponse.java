package com.example.juicy.Model;

public class ConfirmarVentaResponse {
    private int code;
    private String message;
    private VentaData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VentaData getData() {
        return data;
    }

    public void setData(VentaData data) {
        this.data = data;
    }

    public static class VentaData {
        private int id_venta;
        private int id_cliente;
        private int estado;

        public int getId_venta() {
            return id_venta;
        }

        public void setId_venta(int id_venta) {
            this.id_venta = id_venta;
        }

        public int getId_cliente() {
            return id_cliente;
        }

        public void setId_cliente(int id_cliente) {
            this.id_cliente = id_cliente;
        }

        public int getEstado() {
            return estado;
        }

        public void setEstado(int estado) {
            this.estado = estado;
        }
    }
}

