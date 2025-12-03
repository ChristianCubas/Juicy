package com.example.juicy.Model;

import java.util.List;

public class ApiMetodosPagoData {
    private boolean guardado;
    private List<MetodoPagoEntry> metodos;

    public boolean isGuardado() {
        return guardado;
    }

    public List<MetodoPagoEntry> getMetodos() {
        return metodos;
    }
}
