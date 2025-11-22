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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.CarritoItem;
import com.example.juicy.Model.CarritoResponse;
import com.example.juicy.R;
import com.example.juicy.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarritoFragment extends Fragment {

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
        adapter = new CarritoAdapter(requireContext(), listaCarrito, total -> {
            tvSubtotal.setText(String.format(Locale.getDefault(), "Subtotal: S/ %.2f", total));

            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
            prefs.edit().putFloat("totalCarrito", (float) total).apply();
        });
        rvCarrito.setAdapter(adapter);

        btnConfirmar.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_carrito_to_direcciones);
        });

        cargarCarrito();
        return rootView;
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

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        int idCliente = prefs.getInt("idCliente", 0);

        if (token == null || token.trim().isEmpty() || idCliente == 0) {
            Toast.makeText(requireContext(),
                    "Debe iniciar sesión para ver el carrito",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Integer> body = new HashMap<>();
        body.put("id_cliente", idCliente);

        DambJuiceApi apiService = RetrofitClient.getApiService();
        apiService.obtenerCarritoActual("JWT " + token.trim(), body)
                .enqueue(new Callback<CarritoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CarritoResponse> call,
                                           @NonNull Response<CarritoResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Error al cargar carrito (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        CarritoResponse data = response.body();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("totalCarrito", (float) data.getTotalGeneral());
                        if (data.getIdVenta() > 0) {
                            editor.putInt("idVenta", data.getIdVenta());
                        }
                        editor.apply();

                        List<CarritoResponse.Producto> productos = data.getProductos();
                        if (productos != null) {
                            for (CarritoResponse.Producto producto : productos) {
                                String tipo = producto.getTipo();
                                listaCarrito.add(
                                        new CarritoItem(
                                                producto.getNombreProducto(),
                                                tipo == null || tipo.isEmpty() ? "Regular" : tipo,
                                                producto.getCantidad(),
                                                producto.getPrecioTotal()
                                        )
                                );
                            }
                        }

                        tvSubtotal.setText(String.format(Locale.getDefault(),
                                "Subtotal: S/ %.2f", data.getTotalGeneral()));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NonNull Call<CarritoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error al cargar carrito: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
