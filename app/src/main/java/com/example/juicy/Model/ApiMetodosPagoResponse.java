package com.example.juicy.Model;

public class ApiMetodosPagoResponse {
    private int code;
    private String message;
    private ApiMetodosPagoData data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApiMetodosPagoData getData() {
        return data;
    }
}
