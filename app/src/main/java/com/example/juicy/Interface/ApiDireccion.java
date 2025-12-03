package com.example.juicy.Interface;

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


    @POST("eliminar_direccion/{idDireccion}")
    Call<RptaGeneral> eliminarDireccion(
            @Header("Authorization") String authorization,
            @Path("idDireccion") int idDireccion
    );
}
