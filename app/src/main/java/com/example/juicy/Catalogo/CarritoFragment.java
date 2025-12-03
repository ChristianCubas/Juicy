package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.juicy.Model.CarritoItem;
import com.example.juicy.R;
import com.example.juicy.network.VolleySingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarritoFragment extends Fragment {

    private static final String URL_CARRITO =
            com.example.juicy.network.ApiConfig.BASE_URL + "api_lista_carrito";

    private static final String TAG = "CarritoFragment"; // Para logs

    private RecyclerView rvCarrito;
    private TextView tvSubtotal;
    private Button btnConfirmar;
    private CarritoAdapter adapter;
    private final List<CarritoItem> listaCarrito = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_carrito, container, false);

        rvCarrito = rootView.findViewById(R.id.rvCarrito);
        tvSubtotal = rootView.findViewById(R.id.tvSubtotal);
        btnConfirmar = rootView.findViewById(R.id.btnConfirmarCompra);
        setupBottomNavigation(rootView);

        rvCarrito.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CarritoAdapter(requireContext(), listaCarrito, new CarritoAdapter.OnCarritoChangeListener() {
            @Override
            public void onCarritoTotalChanged(double nuevoTotal) {
                // Este se usa si cambias cantidades localmente
                tvSubtotal.setText(String.format("Subtotal: S/ %.2f", nuevoTotal));
            }

            @Override
            public void onEliminarItem(int idDetalle, int position) {
                if (idDetalle == 0) {
                    Toast.makeText(getContext(), "Error: ID de item inválido (0)", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Intento de eliminar item en pos " + position + " con ID 0");
                    return;
                }
                eliminarItemDeBD(idDetalle, position);
            }
        });
        rvCarrito.setAdapter(adapter);

        validarBotonCompra();

        btnConfirmar.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            Bundle args = new Bundle();
            args.putBoolean("mostrarContinuar", true);
            navController.navigate(R.id.action_carrito_to_direcciones, args);
        });

        cargarCarrito();
        return rootView;
    }

    private void validarBotonCompra() {
        if (listaCarrito.isEmpty()) {
            btnConfirmar.setEnabled(false);
            btnConfirmar.setAlpha(0.5f);
            btnConfirmar.setText("Carrito vacío");
        } else {
            btnConfirmar.setEnabled(true);
            btnConfirmar.setAlpha(1.0f);
            btnConfirmar.setText("Confirmar compra");
        }
    }

    private void setupBottomNavigation(View root) {
        BottomNavigationView bottomNav = root.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_carrito);
        bottomNav.setOnItemSelectedListener(item -> {
            int targetDest;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                targetDest = R.id.homeFragment;
            } else if (id == R.id.nav_carrito) {
                targetDest = R.id.carritoFragment;
            } else if (id == R.id.nav_opciones) {
                targetDest = R.id.opcionesFragment;
            } else {
                return false;
            }

            NavController navController = NavHostFragment.findNavController(this);
            androidx.navigation.NavDestination current = navController.getCurrentDestination();
            if (current != null && current.getId() == targetDest) return true;

            navController.navigate(targetDest);
            return true;
        });
    }

    private void cargarCarrito() {
        listaCarrito.clear();
        validarBotonCompra();
        tvSubtotal.setText(String.format(Locale.getDefault(),"Subtotal: S/ %.2f", 0.0));
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("idCliente", 0);

        if (idCliente == 0) return;

        JSONObject body = new JSONObject();
        try { body.put("id_cliente", idCliente); } catch (JSONException ignored) { }

        String URL = com.example.juicy.network.ApiConfig.BASE_URL + "api_lista_carrito";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, body,
                response -> {
                    try {
                        Log.d(TAG, "Respuesta Carrito: " + response.toString()); // <-- MIRA ESTO EN LOGCAT

                        JSONArray productos = response.optJSONArray("productos");
                        double totalGeneral = response.optDouble("total_general", 0);
                        int idVenta = response.optInt("id_venta", 0);

                        if (productos != null && productos.length() > 0) {
                            for (int i = 0; i < productos.length(); i++) {
                                JSONObject obj = productos.getJSONObject(i);

                                // --- AQUÍ ESTÁ LA CLAVE ---
                                // Leemos "id_detalle". Si no viene, asignamos 0.
                                int idDetalle = obj.optInt("id_detalle", 0);

                                if (idDetalle == 0) {
                                    Log.w(TAG, "ALERTA: El producto " + i + " llegó sin id_detalle del servidor");
                                }

                                String nombre = obj.getString("nombre_producto");
                                String personalizaciones = obj.optString("personalizaciones", null);
                                int cantidad = obj.getInt("cantidad");
                                double precio = obj.getDouble("precio_total");

                                // Guardamos en la lista CON el ID correcto
                                listaCarrito.add(new CarritoItem(idDetalle, nombre, personalizaciones, cantidad, precio));
                            }
                        } else {
                            // Si viene vacío, forzamos total en 0 y limpiamos venta
                            totalGeneral = 0;
                            idVenta = 0;
                        }

                        prefs.edit()
                                .putInt("idVenta", idVenta)
                                .putFloat("totalCarrito", (float) totalGeneral)
                                .apply();

                        tvSubtotal.setText(String.format("Subtotal: S/ %.2f", totalGeneral));
                        adapter.notifyDataSetChanged();

                        validarBotonCompra();

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON Error: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(getContext(), "Error al cargar carrito", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    private void eliminarItemDeBD(int idDetalle, int position) {
        String URL_DELETE = com.example.juicy.network.ApiConfig.BASE_URL + "api_eliminar_item_carritoFCN";

        JSONObject body = new JSONObject();
        try { body.put("id_detalle", idDetalle); } catch (JSONException e) { e.printStackTrace(); }

        Log.d(TAG, "Enviando eliminar ID: " + idDetalle); // Log para verificar


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_DELETE, body,
                response -> {
                    try {
                        if (response.getInt("code") == 1) {
                            listaCarrito.remove(position);
                            adapter.notifyItemRemoved(position);

                            // 2. Actualizamos el total
                            double nuevoTotal = response.getDouble("nuevo_total");
                            tvSubtotal.setText(String.format("Subtotal: S/ %.2f", nuevoTotal));

                            SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                            prefs.edit().putFloat("totalCarrito", (float) nuevoTotal).apply();

                            validarBotonCompra();
                            Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            String msg = response.optString("message", "Error desconocido");
                            Toast.makeText(getContext(), "Error API: " + msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error de respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String mensajeError = "Fallo de conexión";

                    if (error.networkResponse != null) {
                        mensajeError = "Error " + error.networkResponse.statusCode; // Ej: Error 404 o 500

                        if (error.networkResponse.data != null) {
                            try {
                                String bodyError = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Log.e(TAG, "Error Body: " + bodyError); // Ver en Logcat
                                JSONObject jsonError = new JSONObject(bodyError);
                                if (jsonError.has("message")) {
                                    mensajeError += ": " + jsonError.getString("message");
                                }
                            } catch (Exception e) {
                            }
                        }
                    } else if (error.getMessage() != null) {
                        mensajeError = error.getMessage();
                    }

                    Toast.makeText(getContext(), mensajeError, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Volley Error: " + error.toString());
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }


}
