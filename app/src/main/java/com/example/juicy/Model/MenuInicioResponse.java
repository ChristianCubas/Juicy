package com.example.juicy.Model;

import java.util.List;

public class MenuInicioResponse {
    private int code;
    private String message;
    private List<Producto> data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Producto> getData() { return data; }
    public void setData(List<Producto> data) { this.data = data; }
}
