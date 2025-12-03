package com.example.juicy.PerfilUsuario;

import static com.example.juicy.R.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.juicy.Model.Compra;
import com.example.juicy.Model.HistorialAdapter;
import com.example.juicy.Model.ProductoHistorial;
import com.example.juicy.R;
import com.example.juicy.network.ApiConfig;
import com.example.juicy.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistorialComprasFragment extends Fragment {

    private RecyclerView recycler;
    private HistorialAdapter adapter;
    private List<Compra> listaCompras = new ArrayList<>();

    private TextView tvVacio;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historial_compras, container, false);

        recycler = v.findViewById(R.id.recyclerHistorial);
        tvVacio = v.findViewById(R.id.tvHistorialVacio);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistorialAdapter(getContext(), listaCompras);
        recycler.setAdapter(adapter);


        cargarHistorial();
        return v;
    }

    private void cargarHistorial() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("idCliente", 0);
        String token = prefs.getString("tokenJWT", "");

        if (idCliente == 0) {
            Toast.makeText(getContext(), "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try { body.put("id_cliente", idCliente); } catch (Exception e) {}

        String url = ApiConfig.BASE_URL + "api_historial_compras";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        if (response.getInt("code") == 1) {
                            JSONArray data = response.getJSONArray("data");
                            listaCompras.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                // 1. Datos de la Venta (Cabecera)
                                Compra c = new Compra();
                                c.setId_venta(obj.getInt("id_venta"));
                                c.setFecha(obj.getString("fecha"));
                                c.setHora(obj.getString("hora"));
                                c.setTotal(obj.getDouble("total"));

                                // 2. Datos de los Productos (Detalle)
                                List<ProductoHistorial> listaProds = new ArrayList<>();
                                JSONArray productosArray = obj.optJSONArray("productos");

                                if (productosArray != null) {
                                    for (int j = 0; j < productosArray.length(); j++) {
                                        JSONObject pObj = productosArray.getJSONObject(j);
                                        ProductoHistorial ph = new ProductoHistorial();
                                        ph.setNombre(pObj.getString("nombre"));
                                        ph.setCantidad(pObj.getInt("cantidad"));
                                        ph.setPrecio(pObj.getDouble("precio"));
                                        listaProds.add(ph);
                                    }
                                }
                                c.setProductos(listaProds);

                                listaCompras.add(c);
                            }

                            if (listaCompras.isEmpty()) {
                                tvVacio.setVisibility(View.VISIBLE);
                            } else {
                                tvVacio.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error al procesar historial", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!token.isEmpty()) headers.put("Authorization", "JWT " + token);
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }
}