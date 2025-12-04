package com.example.juicy.Model;

public class PaypalCaptureResponse {
    private int code;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int id_venta;
        private int id_metodo_pago;
        private String paypal_capture_id;
        private Double monto_pagado;
        private String currency;

        public int getId_venta() {
            return id_venta;
        }

        public int getId_metodo_pago() {
            return id_metodo_pago;
        }

        public String getPaypal_capture_id() {
            return paypal_capture_id;
        }

        public Double getMonto_pagado() {
            return monto_pagado;
        }

        public String getCurrency() {
            return currency;
        }
    }
}
