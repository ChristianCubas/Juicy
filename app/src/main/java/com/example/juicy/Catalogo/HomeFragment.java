package com.example.juicy.Catalogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.juicy.databinding.FragmentHomeBinding;
import com.example.juicy.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private final List<Producto> listaProductos = new ArrayList<>();
    private static final String URL_API = "https://grupotres20252.pythonanywhere.com/api_menu_inicio";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.recyclerProductos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ProductoAdapter(requireContext(), listaProductos, producto -> {
            agregarAlCarrito(producto);
            Toast.makeText(getActivity(),"Producto agregado",Toast.LENGTH_SHORT).show();
        });

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);

        String token = prefs.getString("tokenJWT", null);
        int idCliente = prefs.getInt("idCliente", -1);
        String nombreCliente = prefs.getString("nombreCliente", "Invitado");

        TextView tvUsuario = v.findViewById(R.id.tvUsuario);
        tvUsuario.setText(nombreCliente);

        recyclerView.setAdapter(adapter);

        cargarProductos();
        return v;
    }

    private void cargarProductos() {
        listaProductos.clear();

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        int idCliente = prefs.getInt("idCliente", 0);

        if (token == null || idCliente == 0) {
            Toast.makeText(requireContext(), "Token o id_cliente no disponible. Inicie sesión.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", idCliente);
        } catch (JSONException ignored) {}

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                body,
                response -> {
                    try {
                        if (response.optInt("code", 0) != 1) {
                            Toast.makeText(requireContext(), "Respuesta no OK del servidor.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // data es un OBJETO: { cliente, grupos, categorias[] }
                        JSONObject data = response.getJSONObject("data");
                        JSONArray categorias = data.optJSONArray("categorias");

                        if (categorias != null) {
                            for (int c = 0; c < categorias.length(); c++) {
                                JSONObject cat = categorias.getJSONObject(c);
                                JSONArray productos = cat.optJSONArray("productos");
                                if (productos == null) continue;

                                for (int i = 0; i < productos.length(); i++) {
                                    JSONObject obj = productos.getJSONObject(i);

                                    Producto p = new Producto();
                                    p.setId_producto(obj.getInt("id_producto"));
                                    p.setNombre(obj.getString("nombre_producto"));
                                    p.setDescripcion(obj.optString("descripcion", ""));

                                    String img = obj.optString("imagen", null);
                                    if (img != null && !img.startsWith("http")) {
                                        img = "https://grupotres20252.pythonanywhere.com/" + img.replaceFirst("^/+", "");
                                    }
                                    p.setImagen_url(img);

                                    p.setPrecio(obj.getDouble("precio_base"));
                                    listaProductos.add(p);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Error al procesar datos.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = "Error de conexión con el servidor.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        msg = "HTTP " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + token); // Flask-JWT
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    private void agregarAlCarrito(Producto producto) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("CARRITO_JUICY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Obtenemos el carrito actual
        String carritoJson = prefs.getString("carrito", "[]");

        try {
            JSONArray carritoArray = new JSONArray(carritoJson);
            // Creamos un objeto JSON para el nuevo producto
            JSONObject nuevoProducto = new JSONObject();
            nuevoProducto.put("id_producto", producto.getId_producto());
            nuevoProducto.put("nombre", producto.getNombre());
            nuevoProducto.put("precio", producto.getPrecio());
            nuevoProducto.put("imagen_url", producto.getImagen_url());

            // Agregamos el producto al carrito
            carritoArray.put(nuevoProducto);

            // Guardamos el nuevo carrito
            editor.putString("carrito", carritoArray.toString());
            editor.apply();

            Toast.makeText(requireContext(), producto.getNombre() + " agregado al carrito 🛒", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Error al agregar al carrito", Toast.LENGTH_SHORT).show();
        }
    }

}
