package com.example.juicy.Model;

public class PaypalCreateOrderResponse {
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
        private String orderId;
        private String approvalUrl;
        private int id_venta;
        private double monto;
        private String currency;

        public String getOrderId() {
            return orderId;
        }

        public String getApprovalUrl() {
            return approvalUrl;
        }

        public int getId_venta() {
            return id_venta;
        }

        public double getMonto() {
            return monto;
        }

        public String getCurrency() {
            return currency;
        }
    }
}
