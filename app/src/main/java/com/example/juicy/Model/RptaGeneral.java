package com.example.juicy.model;

import com.google.gson.annotations.SerializedName;

public class RptaGeneral {
    @SerializedName(value = "code", alternate = {"status"})
    private int code;
    @SerializedName(value = "message", alternate = {"mensaje", "error"})
    private String message;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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
}
