package com.example.juicy.Interface;

import com.example.juicy.Catalogo.ResponseDirecciones;
import com.example.juicy.Model.ActualizarPasswordRequest;
import com.example.juicy.Model.AplicarCuponRequest;
import com.example.juicy.Model.AplicarCuponResponse;
import com.example.juicy.Model.PaypalComprobanteRequest;
import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.Model.CarritoResponse;
import com.example.juicy.Model.ConfirmarVentaRequest;
import com.example.juicy.Model.ConfirmarVentaResponse;
import com.example.juicy.Model.MenuInicioResponse;
import com.example.juicy.Model.MeResponse;
import com.example.juicy.Model.RecuperarRequest;
import com.example.juicy.Model.RegistrarClienteRequest;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.Model.ValidarCodigoRequest;
import com.example.juicy.Model.VerificacionCodigoRequest;
import com.example.juicy.Model.VerificacionEnviarRequest;
import com.example.juicy.Model.VerificacionEstadoRequest;

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

    @POST("/api_aplicar_cupon")
    Call<AplicarCuponResponse> aplicarCupon(
            @Header("Authorization") String token,
            @Body AplicarCuponRequest request
    );



    @POST("/api_enviar_comprobante/{id}")
    Call<RptaGeneral> enviarComprobanteCorreo(
            @Header("Authorization") String token,
            @Path("id") int idVenta
    );

    @POST("/agregar_direccion")
    Call<RptaGeneral> agregarDireccion(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );

    @POST("/actualizar_direccion/{id}")
    Call<RptaGeneral> actualizarDireccion(
            @Header("Authorization") String token,
            @Path("id") int idDireccion,
            @Body Map<String, Object> body
    );

    @POST("/eliminar_direccion/{id}")
    Call<RptaGeneral> eliminarDireccion(
            @Header("Authorization") String token,
            @Path("id") int idDireccion
    );
    @POST("api_enviar_codigo")
    Call<RptaGeneral> enviarCodigo(@Body RecuperarRequest request);

    @POST("api_validar_codigo")
    Call<RptaGeneral> validarCodigo(@Body ValidarCodigoRequest request);

    @POST("api_actualizar_password")
    Call<RptaGeneral> actualizarPassword(@Body ActualizarPasswordRequest request);

    @POST("api_verificacion_estado")
    Call<RptaGeneral> verificarEstadoCuenta(@Body VerificacionEstadoRequest request);

    @POST("api_verificacion_enviar")
    Call<RptaGeneral> reenviarCodigoVerificacion(@Body VerificacionEnviarRequest request);

    @POST("api_verificacion_validar")
    Call<RptaGeneral> validarCodigoVerificacion(@Body VerificacionCodigoRequest request);
}
