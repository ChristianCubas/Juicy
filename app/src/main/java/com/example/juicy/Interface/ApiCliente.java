package com.example.juicy.Interface;

import com.example.juicy.Model.Cliente;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiCliente {
    @POST("registrarCliente")
    Call<Cliente> registrarCliente(@Body Cliente cliente);
}
