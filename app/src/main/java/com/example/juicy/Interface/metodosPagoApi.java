package com.example.juicy.Interface;

import com.example.juicy.model.EliminarMetodoPagoRequest;
import com.example.juicy.model.GuardarMetodoPagoRequest;
import com.example.juicy.model.MetodoPagoVentaRequest;
import com.example.juicy.model.RptaGeneral;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface metodosPagoApi {
    @GET("api_listarMetodosPagoXId_cliente")
    Call<RptaGeneral> listarMetodos(@Header("Authorization") String authorization,
                                    @Query("id_cliente") int idCliente);

    @POST("api_guardarMetodoPago")
    Call<RptaGeneral> guardarMetodo(@Header("Authorization") String authorization,
                                    @Body GuardarMetodoPagoRequest body);

    @POST("api_eliminarMetodoPago")
    Call<RptaGeneral> eliminarMetodo(@Header("Authorization") String authorization,
                                     @Body EliminarMetodoPagoRequest body);

    @POST("api_metodo_pago_venta")
    Call<RptaGeneral> setMetodoPagoVenta(
            @Header("Authorization") String authorization,
            @Body MetodoPagoVentaRequest body
    );

}
