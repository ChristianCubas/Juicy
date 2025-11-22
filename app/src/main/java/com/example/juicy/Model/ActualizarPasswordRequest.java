package com.example.juicy.Model;

public class ActualizarPasswordRequest {
    private String email;
    private String password;  // OJO: se llama igual que en el backend

    public ActualizarPasswordRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
