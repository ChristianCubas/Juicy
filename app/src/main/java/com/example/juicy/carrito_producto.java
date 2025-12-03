package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Model.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class carrito_producto extends Fragment {

    private RecyclerView recyclerView;
    private CarritoAdapter adapter;
    private ArrayList<Producto> carritoList;

    public carrito_producto() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_carrito_producto, container, false);

        // Inicializamos las vistas
        recyclerView = view.findViewById(R.id.recyclerCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Recuperamos los productos del carrito
        carritoList = recuperarCarrito();

        // Configuramos el adapter
        adapter = new CarritoAdapter(carritoList);
        recyclerView.setAdapter(adapter);

        Button botonComprar = view.findViewById(R.id.btnFinalizarCompra);
        if (carritoList == null || carritoList.isEmpty()) {
            botonComprar.setVisibility(View.GONE);
        } else {
            botonComprar.setVisibility(View.VISIBLE);
        }

        botonComprar.setOnClickListener(v->{
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.paymentMethodFragment);
        });

        return view;
    }

    /*
    private ArrayList<Producto> recuperarCarrito() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("CARRITO_JUICY", Context.MODE_PRIVATE);

        String carritoJson = prefs.getString("carrito", "[]");
        ArrayList<Producto> lista = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(carritoJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Producto p = new Producto();
                p.setId_producto(obj.getInt("id_producto"));
                p.setNombre(obj.getString("nombre"));
                p.setPrecio(obj.getDouble("precio"));
                p.setImagen_url(obj.getString("imagen_url"));
                lista.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;
    }*/

    private ArrayList<Producto> recuperarCarrito() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("CARRITO_JUICY", Context.MODE_PRIVATE);

        String carritoJson = prefs.getString("carrito", "[]");
        ArrayList<Producto> lista = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(carritoJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Producto p = new Producto();
                p.setId_producto(obj.getInt("id_producto"));
                p.setNombre(obj.getString("nombre"));
                p.setPrecio(obj.getDouble("precio"));
                p.setCantidad(obj.optInt("cantidad", 1));
                p.setImagen_url(obj.getString("imagen_url"));
                lista.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
