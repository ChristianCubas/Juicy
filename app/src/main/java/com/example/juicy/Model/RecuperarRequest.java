package com.example.juicy.Model;

public class RecuperarRequest {
    private String email;

    public RecuperarRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
