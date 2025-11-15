package com.example.juicy.Interface;

import com.example.juicy.Model.MetodoPagoVentaRequest;
import com.example.juicy.Model.RptaGeneral;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface metodosPagoApi {
    @POST("api_listarMetodosPagoXId_cliente")
    Call<RptaGeneral> listarMetodosPago(@Header("Authorization") String authHeader, @Body int idCliente);

    @POST("api_metodo_pago_venta")
    Call<RptaGeneral> setMetodoPagoVenta(@Header("Authorization") String authHeader, @Body MetodoPagoVentaRequest body);
}
