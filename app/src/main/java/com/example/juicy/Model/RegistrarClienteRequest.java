package com.example.juicy.Model;

public class RegistrarClienteRequest {
    private String email;
    private String password;
    private String nro_dni;
    private String nombre;
    private String ape_paterno;
    private String ape_materno;
    private String celular;
    private String medio_verificacion;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNro_dni(String nro_dni) {
        this.nro_dni = nro_dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApe_paterno(String ape_paterno) {
        this.ape_paterno = ape_paterno;
    }

    public void setApe_materno(String ape_materno) {
        this.ape_materno = ape_materno;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setMedio_verificacion(String medio_verificacion) {
        this.medio_verificacion = medio_verificacion;
    }
}
