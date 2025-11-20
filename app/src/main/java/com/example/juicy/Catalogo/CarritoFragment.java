package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.juicy.Model.CarritoItem;
import com.example.juicy.R;
import com.example.juicy.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoFragment extends Fragment {

    private static final String URL_CARRITO =
            "https://grupotres20252.pythonanywhere.com/api_lista_carrito";

    private RecyclerView rvCarrito;
    private TextView tvSubtotal;
    private Button btnConfirmar;
    private CarritoAdapter adapter;
    private final List<CarritoItem> listaCarrito = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_carrito, container, false);

        // Inicializar las vistas
        rvCarrito = rootView.findViewById(R.id.rvCarrito);
        tvSubtotal = rootView.findViewById(R.id.tvSubtotal);
        btnConfirmar = rootView.findViewById(R.id.btnConfirmarCompra);

        // Configuración del RecyclerView
        rvCarrito.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CarritoAdapter(requireContext(), listaCarrito);
        rvCarrito.setAdapter(adapter);

        // Acción del botón "Confirmar compra"
        btnConfirmar.setOnClickListener(v -> {
            // Obtener el NavController de la vista raíz del fragmento y realizar la navegación
            NavController navController = Navigation.findNavController(rootView); // Usamos rootView aquí
            navController.navigate(R.id.action_carrito_to_direcciones);  // Navega a DireccionesFragment
        });

        // Cargar los datos del carrito
        cargarCarrito();

        return rootView;
    }


    private void cargarCarrito() {
        listaCarrito.clear();

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        int idCliente = prefs.getInt("idCliente", 0);

        if (token == null || idCliente == 0) {
            Toast.makeText(requireContext(),
                    "Debe iniciar sesión para ver el carrito",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", idCliente);
        } catch (JSONException ignored) {}

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_CARRITO,
                body,
                response -> {
                    try {
                        JSONArray productos = response.optJSONArray("productos");
                        double totalGeneral = response.optDouble("total_general", 0);
                        int idVenta = response.optInt("id_venta", 0);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("totalCarrito", (float) totalGeneral);
                        if (idVenta > 0) {
                            editor.putInt("idVenta", idVenta);
                        }
                        editor.apply();

                        if (productos != null) {
                            for (int i = 0; i < productos.length(); i++) {
                                JSONObject obj = productos.getJSONObject(i);
                                String nombre = obj.getString("nombre_producto");
                                String tipo   = obj.optString("tipo", "Regular");
                                int cantidad  = obj.getInt("cantidad");
                                double precio = obj.getDouble("precio_total");

                                listaCarrito.add(
                                        new CarritoItem(nombre, tipo, cantidad, precio)
                                );
                            }
                        }

                        tvSubtotal.setText(String.format("Subtotal: S/ %.2f", totalGeneral));
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Toast.makeText(requireContext(),
                                "Error al procesar carrito", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String msg = "Error al cargar carrito.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        msg = "HTTP " + error.networkResponse.statusCode + ": " +
                                new String(error.networkResponse.data);
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + token);   // igual que en HomeFragment
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext())
                .getRequestQueue().add(request);
    }
}
