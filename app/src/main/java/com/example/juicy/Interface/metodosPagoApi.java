package com.example.juicy.Interface;

import com.example.juicy.Model.ApiMetodosPagoRequest;
import com.example.juicy.Model.ApiMetodosPagoResponse;
import com.example.juicy.Model.EliminarMetodoPagoRequest;
import com.example.juicy.Model.GuardarMetodoPagoRequest;
import com.example.juicy.Model.MetodoPagoVentaRequest;
import com.example.juicy.Model.RptaGeneral;

// 🔹 modelos PayPal como clases normales
import com.example.juicy.Model.PaypalCreateOrderRequest;
import com.example.juicy.Model.PaypalCreateOrderResponse;
import com.example.juicy.Model.PaypalCaptureRequest;
import com.example.juicy.Model.PaypalCaptureResponse;

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

    @POST("api_metodos_pago")
    Call<ApiMetodosPagoResponse> apiMetodosPago(
            @Header("Authorization") String authorization,
            @Body ApiMetodosPagoRequest body
    );

    // 🔹 CREAR ORDEN PAYPAL
    @POST("api_paypal_create_order")
    Call<PaypalCreateOrderResponse> createPaypalOrder(
            @Header("Authorization") String auth,
            @Body PaypalCreateOrderRequest body
    );

    // 🔹 CAPTURAR ORDEN PAYPAL
    @POST("api_paypal_capture_order")
    Call<PaypalCaptureResponse> capturePaypalOrder(
            @Header("Authorization") String auth,
            @Body PaypalCaptureRequest body
    );
}
