package com.example.juicy.Interface;

import com.example.juicy.Catalogo.MetodoPago;
import com.example.juicy.Catalogo.ResponseDirecciones;
import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.Model.EliminarMetodoPagoRequest;
import com.example.juicy.Model.GuardarMetodoPagoRequest;
import com.example.juicy.Model.MenuInicioResponse;
import com.example.juicy.Model.MeResponse;
import com.example.juicy.Model.MetodoPagoVentaRequest;
import com.example.juicy.Model.RegistrarClienteRequest;
import com.example.juicy.Model.RptaGeneral;

import java.util.List;
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

    @POST("/api_listar_direcciones")
    Call<ResponseDirecciones> listarDirecciones(
            @Header("Authorization") String token,
            @Body Map<String, Integer> body
    );

    @POST("api_listarMetodosPagoXId_cliente")
    Call<RptaGeneral> listarMetodosPago(@Header("Authorization") String authHeader, @Body Map<String, Integer> body);


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
