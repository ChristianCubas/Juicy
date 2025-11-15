package com.example.juicy.Interface;
import com.example.juicy.Model.ApiMetodosPagoRequest;
import com.example.juicy.Model.MetodoPagoVentaRequest;
import com.example.juicy.Model.RptaGeneral;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
public interface ApiMetodosPago {
    @POST("api_metodos_pago")
    Call<RptaGeneral> metodosPago(
            @Header("Authorization") String auth,
            @Body ApiMetodosPagoRequest body
    );

    @POST("api_metodo_pago_venta")
    Call<RptaGeneral> setMetodoPagoVenta(
            @Header("Authorization") String auth,
            @Body MetodoPagoVentaRequest body
    );
}
