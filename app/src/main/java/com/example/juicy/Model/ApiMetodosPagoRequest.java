package com.example.juicy.Model;

public class ApiMetodosPagoRequest {

    private int id_cliente;
    private boolean guardar;
    private NuevoMetodo nuevo_metodo;   // ya NO es Object

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public boolean isGuardar() {
        return guardar;
    }

    public void setGuardar(boolean guardar) {
        this.guardar = guardar;
    }

    public NuevoMetodo getNuevo_metodo() {
        return nuevo_metodo;
    }

    public void setNuevo_metodo(NuevoMetodo nuevo_metodo) {
        this.nuevo_metodo = nuevo_metodo;
    }

    // Clase interna que representa el JSON:
    // "nuevo_metodo": { ... }
    public static class NuevoMetodo {
        private String titular;
        private String num_tarjeta;
        private String fecha_expiracion;
        private String cvv;
        private String cod_paypal;

        public String getTitular() {
            return titular;
        }

        public void setTitular(String titular) {
            this.titular = titular;
        }

        public String getNum_tarjeta() {
            return num_tarjeta;
        }

        public void setNum_tarjeta(String num_tarjeta) {
            this.num_tarjeta = num_tarjeta;
        }

        public String getFecha_expiracion() {
            return fecha_expiracion;
        }

        public void setFecha_expiracion(String fecha_expiracion) {
            this.fecha_expiracion = fecha_expiracion;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public String getCod_paypal() {
            return cod_paypal;
        }

        public void setCod_paypal(String cod_paypal) {
            this.cod_paypal = cod_paypal;
        }
    }
}
