package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Model.Producto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Carrito_producto extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarritoAdapter adapter;
    private ArrayList<Producto> carritoList;
    private TextView tvSubtotal;
    private Button btnConfirmarCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_carrito_producto);

        recyclerView = findViewById(R.id.recyclerCarrito);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        btnConfirmarCompra = findViewById(R.id.btnConfirmarCompra);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carritoList = recuperarCarrito();

        if (carritoList == null) {
            carritoList = new ArrayList<>();
        }

        adapter = new CarritoAdapter(carritoList);
        recyclerView.setAdapter(adapter);

        calcularSubtotal();

        btnConfirmarCompra.setOnClickListener(v -> {
            // Aquí podrías limpiar el carrito o confirmar compra
        });
    }

    private ArrayList<Producto> recuperarCarrito() {
        SharedPreferences prefs = getSharedPreferences("CARRITO_JUICY", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString("carrito", "[]");
        Type type = new TypeToken<ArrayList<Producto>>(){}.getType();
        ArrayList<Producto> lista = gson.fromJson(json, type);

        Log.d("CarritoJuicy", "Productos recuperados: " + json);

        return (lista != null) ? lista : new ArrayList<>();
    }

    private void calcularSubtotal() {
        double subtotal = 0.0;
        for (Producto p : carritoList) {
            subtotal += p.getPrecio();
        }
        tvSubtotal.setText(String.format("S/. %.2f", subtotal));
    }
}
