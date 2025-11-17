package com.example.juicy.Interface;

import com.example.juicy.Catalogo.Direccion;
import com.example.juicy.Catalogo.ResponseDirecciones;
import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.Model.CarritoResponse;
import com.example.juicy.Model.ConfirmarVentaRequest;
import com.example.juicy.Model.ConfirmarVentaResponse;
import com.example.juicy.Model.MenuInicioResponse;
import com.example.juicy.Model.MeResponse;
import com.example.juicy.Model.RegistrarClienteRequest;
import com.example.juicy.Model.RptaGeneral;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("/api_lista_carrito")
    Call<CarritoResponse> obtenerCarritoActual(
            @Header("Authorization") String token,
            @Body Map<String, Integer> body
    );

    @POST("/api_confirmarventa")
    Call<ConfirmarVentaResponse> confirmarVenta(
            @Header("Authorization") String token,
            @Body ConfirmarVentaRequest request
    );

    @GET("/api_venta_pdf/{id}")
    Call<ResponseBody> obtenerPdfVenta(
            @Header("Authorization") String token,
            @Path("id") int idVenta
    );

    @POST("/api_enviar_comprobante/{id}")
    Call<RptaGeneral> enviarComprobanteCorreo(
            @Header("Authorization") String token,
            @Path("id") int idVenta
    );
}
