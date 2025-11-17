package com.example.juicy.Catalogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
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
    private ProductoAdapter adapter;
    TextView textNombre;
    private final List<Producto> listaProductos = new ArrayList<>();

    private static final String URL_API = "https://grupotres20252.pythonanywhere.com/api_menu_inicio";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);   // 👉 Habilitar menú en Fragment
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Oculta la flecha de regresar
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.recyclerProductos);
        textNombre = v.findViewById(R.id.usuario);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductoAdapter(requireContext(), listaProductos, producto -> {
            Intent intent = new Intent(getActivity(), DetalleProductoActivity.class);

            intent.putExtra("ID_PRODUCTO", producto.getIdProducto());

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        cargarProductos();

        return v;
    }

    // -------------------------------------------------------------------
    //  🔥 BOTÓN "Carrito" en el menú (arriba derecha)
    // -------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);  // tu menú
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_carrito) {
            // Navegar al fragmento del carrito
            NavHostFragment.findNavController(this)
                    .navigate(R.id.carritoFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // -------------------------------------------------------------------
    // 🔥 CARGAR PRODUCTOS DESDE EL API
    // -------------------------------------------------------------------
    private void cargarProductos() {
        listaProductos.clear();

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        String nombre = prefs.getString("nombreCliente", null);
        int idCliente = prefs.getInt("idCliente", 0);
        textNombre.setText(nombre);

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
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }
}
