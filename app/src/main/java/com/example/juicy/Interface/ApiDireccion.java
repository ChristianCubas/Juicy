package com.example.juicy.Interface;

import com.example.juicy.Model.ActualizarDireccionRequest;
import com.example.juicy.Model.AgregarDireccionRequest;
import com.example.juicy.Model.RptaGeneral;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiDireccion {
    // Mis direcciones (GET con path param)
    @GET("listar_direcciones/{idUsuario}")
    Call<RptaGeneral> listarDirecciones(
            @Header("Authorization") String authorization,
            @Path("idUsuario") int idUsuario
    );

    @POST("agregar_direccion")
    Call<RptaGeneral> agregarDireccion(
            @Header("Authorization") String authorization,
            @Body AgregarDireccionRequest body
    );

    @POST("actualizar_direccion/{idDireccion}")
    Call<RptaGeneral> actualizarDireccion(
            @Header("Authorization") String authorization,
            @Path("idDireccion") int idDireccion,
            @Body ActualizarDireccionRequest body
    );

    @POST("eliminar_direccion/{idDireccion}")
    Call<RptaGeneral> eliminarDireccion(
            @Header("Authorization") String authorization,
            @Path("idDireccion") int idDireccion
    );
}
