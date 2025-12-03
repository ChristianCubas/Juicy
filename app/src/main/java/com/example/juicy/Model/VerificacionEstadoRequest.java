package com.example.juicy.Model;

public class VerificacionEstadoRequest {
    private String email;

    public VerificacionEstadoRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
