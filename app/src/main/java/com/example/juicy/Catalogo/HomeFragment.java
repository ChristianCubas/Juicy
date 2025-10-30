package com.example.juicy.Catalogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.juicy.R;
import com.example.juicy.Model.Producto;
import com.example.juicy.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout layoutLoading;
    private ProductoAdapter adapter;
    private List<Producto> listaProductos = new ArrayList<>();

    private static final String URL_API = "https://grupotres20252.pythonanywhere.com/api_menu_inicio";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.recyclerProductos);


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductoAdapter(getContext(), listaProductos);
        recyclerView.setAdapter(adapter);

        cargarProductos();
        return v;
    }

    private void cargarProductos() {
        layoutLoading.setVisibility(View.VISIBLE);
        listaProductos.clear();

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);

        if (token == null) {
            Toast.makeText(getContext(), "Token no disponible. Inicie sesión.", Toast.LENGTH_SHORT).show();
            layoutLoading.setVisibility(View.GONE);
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", prefs.getInt("idCliente", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                body,
                response -> {
                    layoutLoading.setVisibility(View.GONE);
                    try {
                        if (response.getInt("code") == 1) {
                            JSONArray productos = response.getJSONArray("data");
                            for (int i = 0; i < productos.length(); i++) {
                                JSONObject obj = productos.getJSONObject(i);
                                Producto p = new Producto();
                                p.setId_producto(obj.getInt("id"));
                                p.setNombre(obj.getString("nombre"));
                                p.setDescripcion(obj.getString("descripcion"));
                                p.setImagen_url(obj.getString("imagen"));
                                p.setPrecio(obj.getDouble("precio"));
                                listaProductos.add(p);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No se encontraron productos.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar datos.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error de conexión con el servidor.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).getRequestQueue();
    }
}
