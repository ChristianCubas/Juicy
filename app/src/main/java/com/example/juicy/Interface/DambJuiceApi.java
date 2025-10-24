package com.example.juicy.Interface;

import com.example.juicy.model.AuthRequest;
import com.example.juicy.model.AuthResponse;
import com.example.juicy.model.RegistrarClienteRequest;
import com.example.juicy.model.RptaGeneral;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DambJuiceApi {
    @POST("auth")
    Call<AuthResponse> obtenerToken(@Body AuthRequest authRequest);

    @POST("api_registrar_cliente")
    Call<RptaGeneral> registrarCliente(@Body RegistrarClienteRequest request);
}
