package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Model.CarritoResponse;
import com.example.juicy.Model.ConfirmarVentaRequest;
import com.example.juicy.Model.ConfirmarVentaResponse;
import com.example.juicy.R;
import com.example.juicy.databinding.FragmentResumenBinding;
import com.example.juicy.network.RetrofitClient;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumenFragment extends Fragment {

    private FragmentResumenBinding binding;
    private boolean sincronizandoVentaActiva = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResumenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fillSummary(getArguments());
        binding.btnConfirmarPago.setOnClickListener(v -> confirmarVenta());
    }

    private void fillSummary(@Nullable Bundle bundle) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);

        float total = prefs.getFloat("totalCarrito", 0f);
        binding.totalText.setText(
                String.format(Locale.getDefault(), "S/. %.2f", total)
        );

        String metodoTexto = bundle != null ? bundle.getString("metodo_pago") : null;
        int idMetodo = prefs.getInt("idMetodoPagoSeleccionado", -1);
        if (TextUtils.isEmpty(metodoTexto) && idMetodo > 0) {
            metodoTexto = "Metodo #" + idMetodo;
        }
        binding.metodoPagoText.setText(
                TextUtils.isEmpty(metodoTexto) ? "Metodo no disponible" : metodoTexto
        );

        String direccionTexto = bundle != null ? bundle.getString("direccion_texto") : null;
        if (TextUtils.isEmpty(direccionTexto)) {
            direccionTexto = prefs.getString("direccionTexto", null);
        }
        int idDireccion = prefs.getInt("direccionSeleccionada", -1);
        if (TextUtils.isEmpty(direccionTexto) && idDireccion > 0) {
            direccionTexto = "Direccion #" + idDireccion;
        }
        binding.entregaText.setText(
                TextUtils.isEmpty(direccionTexto) ? "Entrega no disponible" : direccionTexto
        );
    }

    private void confirmarVenta() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);

        int idCliente = prefs.getInt("idCliente", 0);
        int idVenta = prefs.getInt("idVenta", 0);
        int idDireccion = prefs.getInt("direccionSeleccionada", -1);
        int idMetodoPago = prefs.getInt("idMetodoPagoSeleccionado", -1);
        String token = prefs.getString("tokenJWT", null);
        float total = prefs.getFloat("totalCarrito", 0f);

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No se encontro el token de sesion.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idCliente <= 0 || idDireccion <= 0 || idMetodoPago <= 0) {
            String missing = "Datos incompletos -> cliente:" + idCliente
                    + " venta:" + idVenta
                    + " direccion:" + idDireccion
                    + " metodo:" + idMetodoPago;
            Toast.makeText(requireContext(), missing, Toast.LENGTH_LONG).show();
            return;
        }
        if (total <= 0f) {
            Toast.makeText(requireContext(), "No se puede confirmar una venta vacia.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idVenta <= 0) {
            sincronizarVentaActiva(idCliente, token);
            return;
        }

        toggleConfirmButton(false, true);

        ConfirmarVentaRequest request = new ConfirmarVentaRequest(
                idCliente, idVenta, idDireccion, idMetodoPago
        );

        RetrofitClient.getApiService()
                .confirmarVenta("JWT " + token.trim(), request)
                .enqueue(new Callback<ConfirmarVentaResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ConfirmarVentaResponse> call,
                                           @NonNull Response<ConfirmarVentaResponse> response) {
                        toggleConfirmButton(true, false);

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Error al confirmar: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ConfirmarVentaResponse body = response.body();
                        Toast.makeText(requireContext(), body.getMessage(), Toast.LENGTH_SHORT).show();

                        if (body.getCode() == 1 && body.getData() != null) {
                            ConfirmarVentaResponse.VentaData data = body.getData();
                            Toast.makeText(requireContext(),
                                    "Venta #" + data.getId_venta() + " confirmada.",
                                    Toast.LENGTH_LONG).show();
                            navegarAConfirmacion(data);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ConfirmarVentaResponse> call,
                                          @NonNull Throwable t) {
                        toggleConfirmButton(true, false);
                        Toast.makeText(requireContext(),
                                "No se pudo confirmar la venta: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleConfirmButton(boolean enabled, boolean showProgress) {
        if (binding == null) {
            return;
        }
        binding.btnConfirmarPago.setEnabled(enabled);
        binding.btnConfirmarPago.setAlpha(enabled ? 1f : 0.6f);
        binding.btnConfirmarPago.setText(showProgress ? "Confirmando..." : "Confirmar pago");
    }

    private void navegarAConfirmacion(@NonNull ConfirmarVentaResponse.VentaData data) {
        if (!isAdded()) {
            return;
        }
        Bundle args = new Bundle();
        args.putInt(ConfirmacionPedidoFragment.ARG_ID_VENTA, data.getId_venta());
        args.putDouble(ConfirmacionPedidoFragment.ARG_TOTAL, obtenerTotalPedido());
        args.putString(ConfirmacionPedidoFragment.ARG_DIRECCION, binding != null ? binding.entregaText.getText().toString() : "");
        args.putString(ConfirmacionPedidoFragment.ARG_METODO, binding != null ? binding.metodoPagoText.getText().toString() : "");

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_resumenFragment_to_confirmacionPedidoFragment, args);
    }

    private double obtenerTotalPedido() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        return prefs.getFloat("totalCarrito", 0f);
    }

    private void sincronizarVentaActiva(int idCliente, @NonNull String token) {
        if (sincronizandoVentaActiva) {
            Toast.makeText(requireContext(), "Sincronizando carrito...", Toast.LENGTH_SHORT).show();
            return;
        }
        sincronizandoVentaActiva = true;
        toggleConfirmButton(false, true);

        Map<String, Integer> body = new HashMap<>();
        body.put("id_cliente", idCliente);

        RetrofitClient.getApiService()
                .obtenerCarritoActual("JWT " + token.trim(), body)
                .enqueue(new Callback<CarritoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CarritoResponse> call,
                                           @NonNull Response<CarritoResponse> response) {
                        sincronizandoVentaActiva = false;

                        if (!response.isSuccessful() || response.body() == null) {
                            toggleConfirmButton(true, false);
                            Toast.makeText(requireContext(),
                                    "No se pudo sincronizar el carrito (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        CarritoResponse body = response.body();
                        if (body.getIdVenta() <= 0) {
                            toggleConfirmButton(true, false);
                            Toast.makeText(requireContext(),
                                    "No hay un carrito activo para confirmar.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences prefs = requireActivity()
                                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                        prefs.edit()
                                .putInt("idVenta", body.getIdVenta())
                                .putFloat("totalCarrito", (float) body.getTotalGeneral())
                                .apply();

                        fillSummary(getArguments());
                        confirmarVenta();
                    }

                    @Override
                    public void onFailure(@NonNull Call<CarritoResponse> call,
                                          @NonNull Throwable t) {
                        sincronizandoVentaActiva = false;
                        toggleConfirmButton(true, false);
                        Toast.makeText(requireContext(),
                                "Error al sincronizar carrito: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
