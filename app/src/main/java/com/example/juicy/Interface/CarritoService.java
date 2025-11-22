package com.example.juicy.Interface;

import com.example.juicy.AgregarAlCarrito;
import com.example.juicy.Model.CarritoItem;
import com.example.juicy.Model.CarritoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CarritoService {
    @POST("/api_agregar_al_carrito")
    Call<AgregarAlCarrito> agregarAlCarrito(
            @Header("Authorization") String token,
            @Body AgregarAlCarrito request
    );

    @POST("/api_lista_carrito")
    Call<CarritoItem> listarCarrito(
            @Header("Authorization") String token,
            @Body CarritoItem body
    );
}
