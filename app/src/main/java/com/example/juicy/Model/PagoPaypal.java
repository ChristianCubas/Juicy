package com.example.juicy.Model;

public class PagoPaypal {
    public static class PaypalCreateOrderRequest {
        private int id_cliente;

        public PaypalCreateOrderRequest(int id_cliente) {
            this.id_cliente = id_cliente;
        }

        public int getId_cliente() { return id_cliente; }
        public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    }

    public static class PaypalCreateOrderResponse {
        private String orderId;
        private String approvalUrl;

        public String getOrderId() { return orderId; }
        public String getApprovalUrl() { return approvalUrl; }
    }

    public static class PaypalCaptureRequest {
        private String orderId;

        public PaypalCaptureRequest(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderId() { return orderId; }
    }

    public static class PaypalCaptureResponse {
        private int code;
        private String message;

        public int getCode() { return code; }
        public String getMessage() { return message; }
    }

}
