package com.example.juicy.Interface;

import com.example.juicy.model.AuthRequest;
import com.example.juicy.model.AuthResponse;
import com.example.juicy.model.GuardarDireccionesRequest;
import com.example.juicy.model.RptaGeneral;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ronaldApi {

    @POST("auth")
    Call<AuthResponse> obtenerToken(@Body AuthRequest authRequest);


    @POST("api_r")
    Call<RptaGeneral> guardarDireccion(@Header("Authorization") String authorization,
                                     @Body GuardarDireccionesRequest guardarDireccionesRequest);
}
