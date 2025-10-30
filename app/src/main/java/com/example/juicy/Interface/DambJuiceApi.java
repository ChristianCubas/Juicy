package com.example.juicy.Interface;

import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.Model.MenuInicioResponse;
import com.example.juicy.Model.MeResponse;
import com.example.juicy.Model.RegistrarClienteRequest;
import com.example.juicy.Model.RptaGeneral;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DambJuiceApi {
    @POST("auth")
    Call<AuthResponse> obtenerToken(@Body AuthRequest authRequest);

    @POST("api_registrar_cliente")
    Call<RptaGeneral> registrarCliente(@Body RegistrarClienteRequest request);

    @GET("api_me")
    Call<MeResponse> me(@Header("Authorization") String authHeader);

    @POST("/api_menu_inicio")
    Call<MenuInicioResponse> getMenuInicio(
            @Header("Authorization") String token,
            @Body Map<String, Integer> body
    );
}
