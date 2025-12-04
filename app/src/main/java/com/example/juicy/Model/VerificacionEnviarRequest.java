package com.example.juicy.Model;

public class VerificacionEnviarRequest {
    private String email;
    private String medio;

    public VerificacionEnviarRequest(String email, String medio) {
        this.email = email;
        this.medio = medio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedio() {
        return medio;
    }

    public void setMedio(String medio) {
        this.medio = medio;
    }
}
