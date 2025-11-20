package com.example.juicy.Model;

import java.util.List;

public class ApiMetodosPagoResponse {

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
        private boolean guardado;
        private List<MetodoPagoEntry> metodos;

        public boolean isGuardado() {
            return guardado;
        }

        public List<MetodoPagoEntry> getMetodos() {
            return metodos;
        }
    }
}
