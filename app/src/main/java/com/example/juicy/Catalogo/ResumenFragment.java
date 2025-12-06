package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.juicy.Interface.metodosPagoApi;
import com.example.juicy.Model.AplicarCuponRequest;
import com.example.juicy.Model.AplicarCuponResponse;
import com.example.juicy.Model.CarritoResponse;
import com.example.juicy.Model.ConfirmarVentaRequest;
import com.example.juicy.Model.ConfirmarVentaResponse;
import com.example.juicy.Model.PaypalCreateOrderRequest;
import com.example.juicy.Model.PaypalCreateOrderResponse;
import com.example.juicy.R;
import com.example.juicy.databinding.FragmentResumenBinding;
import com.example.juicy.network.ApiConfig;
import com.example.juicy.network.RetrofitClient;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResumenFragment extends Fragment {

    private FragmentResumenBinding binding;
    private boolean sincronizandoVentaActiva = false;
    private ResumenProductosAdapter resumenAdapter;

    // PayPal
    private metodosPagoApi metodosPagoApi;
    private String authHeaderPaypal;
    private int idClientePaypal;

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

        // Recycler
        resumenAdapter = new ResumenProductosAdapter();
        binding.recyclerViewProductos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewProductos.setAdapter(resumenAdapter);

        // Cupón
        binding.btnAplicarCupon.setOnClickListener(v -> aplicarCupon());

        // Llenar textos iniciales desde SP + argumentos
        fillSummary(getArguments());

        // Cargar carrito desde API
        cargarResumenDesdeApi();

        // ==== INICIALIZAR API PAYPAL ====
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        idClientePaypal = prefs.getInt("idCliente", 0);
        String token = prefs.getString("tokenJWT", "");

        if (!TextUtils.isEmpty(token) && idClientePaypal > 0) {
            authHeaderPaypal = "JWT " + token.trim();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            metodosPagoApi = retrofit.create(metodosPagoApi.class);
        }

        // Botón confirmar pago
        binding.btnConfirmarPago.setOnClickListener(v -> {
            String metodo = binding.metodoPagoText.getText().toString();

            if (metodo != null && metodo.toLowerCase().contains("paypal")) {
                // 👉 Si el método elegido es PayPal, primero llamamos a PayPal
                iniciarPagoPaypal();
            } else {
                // 👉 Para VISA u otros métodos, flujo normal
                confirmarVenta();
            }
        });

        // Si venimos desde el deep link de PayPal y ya se capturó el pago
        Bundle args = getArguments();
        if (args != null && args.getBoolean("paypal_pagado", false)) {
            // Ya se cobró en PayPal, solo falta cerrar la venta
            confirmarVenta();
        }
    }

    // ================== RESUMEN / TEXTO ==================

    private void fillSummary(@Nullable Bundle bundle) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);

        float total = prefs.getFloat("totalCarrito", 0f);

        binding.tvSubtotal.setText(String.format(Locale.getDefault(), "S/. %.2f", total));
        binding.totalText.setText(String.format(Locale.getDefault(), "S/. %.2f", total));

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

    // ================== CUPÓN ==================

    private void aplicarCupon() {
        String codigo = binding.etCupon.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(codigo)) {
            binding.etCupon.setError("Ingresa un código");
            return;
        }

        // Ocultar teclado
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("idCliente", 0);
        String token = prefs.getString("tokenJWT", "");

        binding.btnAplicarCupon.setEnabled(false);
        binding.btnAplicarCupon.setText("...");

        AplicarCuponRequest request = new AplicarCuponRequest(idCliente, codigo);

        RetrofitClient.getApiService().aplicarCupon("JWT " + token, request)
                .enqueue(new Callback<AplicarCuponResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AplicarCuponResponse> call,
                                           @NonNull Response<AplicarCuponResponse> response) {
                        if (binding == null) return;

                        binding.btnAplicarCupon.setEnabled(true);
                        binding.btnAplicarCupon.setText("Aplicar");

                        if (response.isSuccessful() && response.body() != null) {
                            AplicarCuponResponse data = response.body();
                            if (data.getCode() == 1) {
                                mostrarMensajeCupon(data.getMessage(), true);
                                actualizarPreciosUI(
                                        data.getSubtotal(),
                                        data.getDescuento(),
                                        data.getTotal_final()
                                );

                                binding.etCupon.setEnabled(false);
                                binding.btnAplicarCupon.setEnabled(false);
                                binding.btnAplicarCupon.setText("OK");
                            } else {
                                mostrarMensajeCupon(data.getMessage(), false);
                            }
                        } else {
                            Log.d("CUPON_LOG", "Enviando cupón: " + codigo + " para cliente: " + idCliente);
                            Log.e("CUPON_LOG", "Error API: " + response.code() + " - " + response.message());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e("CUPON_LOG", "Error Body: " + response.errorBody().string());
                                }
                            } catch (Exception e) { /* ignore */ }
                            mostrarMensajeCupon("Error al aplicar cupón", false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AplicarCuponResponse> call,
                                          @NonNull Throwable t) {
                        if (binding == null) return;
                        binding.btnAplicarCupon.setEnabled(true);
                        binding.btnAplicarCupon.setText("Aplicar");
                        mostrarMensajeCupon("Error de conexión", false);
                    }
                });
    }

    private void mostrarMensajeCupon(String msg, boolean exito) {
        binding.tvMensajeCupon.setVisibility(View.VISIBLE);
        binding.tvMensajeCupon.setText(msg);
        binding.tvMensajeCupon.setTextColor(
                exito ? Color.parseColor("#4CAF50") : Color.RED
        );
    }

    private void actualizarPreciosUI(double subtotal, double descuento, double total) {
        binding.tvSubtotal.setText(String.format(Locale.getDefault(), "S/. %.2f", subtotal));
        binding.totalText.setText(String.format(Locale.getDefault(), "S/. %.2f", total));

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        prefs.edit().putFloat("totalCarrito", (float) total).apply();

        if (descuento > 0) {
            binding.layoutDescuento.setVisibility(View.VISIBLE);
            binding.tvDescuento.setText(String.format(Locale.getDefault(), "- S/. %.2f", descuento));
        } else {
            binding.layoutDescuento.setVisibility(View.GONE);
        }
    }

    // ================== CONFIRMAR VENTA ==================

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

                        if (body.getCode() == 1 && body.getData() != null) {
                            ConfirmarVentaResponse.VentaData data = body.getData();
                            Toast.makeText(requireContext(),
                                    "¡Pedido realizado con éxito!",
                                    Toast.LENGTH_LONG).show();
                            navegarAConfirmacion(data);
                        } else {
                            Toast.makeText(requireContext(), body.getMessage(), Toast.LENGTH_SHORT).show();
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
        args.putString(ConfirmacionPedidoFragment.ARG_DIRECCION,
                binding != null ? binding.entregaText.getText().toString() : "");
        args.putString(ConfirmacionPedidoFragment.ARG_METODO,
                binding != null ? binding.metodoPagoText.getText().toString() : "");

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

                        if (binding != null) {
                            double subtotal = body.getSubtotalSinDescuento() > 0
                                    ? body.getSubtotalSinDescuento()
                                    : body.getTotalGeneral();
                            actualizarPreciosUI(subtotal,
                                    body.getDescuentoAplicado(),
                                    body.getTotalGeneral());
                        }
                        if (resumenAdapter != null) {
                            resumenAdapter.setData(body.getProductos());
                        }

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

    private void cargarResumenDesdeApi() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);

        int idCliente = prefs.getInt("idCliente", 0);
        String token = prefs.getString("tokenJWT", null);

        if (idCliente == 0 || token == null || token.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No se pudo cargar el carrito del cliente.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Integer> body = new HashMap<>();
        body.put("id_cliente", idCliente);

        RetrofitClient.getApiService()
                .obtenerCarritoActual("JWT " + token.trim(), body)
                .enqueue(new Callback<CarritoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CarritoResponse> call,
                                           @NonNull Response<CarritoResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "No se pudo actualizar el resumen (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        CarritoResponse data = response.body();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("idVenta", data.getIdVenta());

                        float totalApi = (float) data.getTotalGeneral();

                        // Si ya hubo un cupón aplicado NO pisar el total guardado
                        float totalGuardado = prefs.getFloat("totalCarrito", -1);

                        if (data.getDescuentoAplicado() > 0) {
                            // API ya devuelve total con descuento
                            totalGuardado = totalApi;
                        }

                        if (totalGuardado == -1) {
                            // Primera vez
                            totalGuardado = totalApi;
                        }

                        editor.putFloat("totalCarrito", totalGuardado).apply();

                        editor.apply();

                        if (binding != null) {
                            double subtotal = data.getSubtotalSinDescuento() > 0
                                    ? data.getSubtotalSinDescuento()
                                    : data.getTotalGeneral();
                            actualizarPreciosUI(subtotal,
                                    data.getDescuentoAplicado(),
                                    data.getTotalGeneral());
                        }
                        if (resumenAdapter != null) {
                            resumenAdapter.setData(data.getProductos());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CarritoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error al cargar resumen: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================== PAYPAL (crear orden y abrir navegador) ==================

    private void iniciarPagoPaypal() {
        if (metodosPagoApi == null || TextUtils.isEmpty(authHeaderPaypal) || idClientePaypal <= 0) {
            Toast.makeText(requireContext(),
                    "Sesión inválida para pagar con PayPal.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        toggleConfirmButton(false, true); // "Confirmando..."

        PaypalCreateOrderRequest body = new PaypalCreateOrderRequest(idClientePaypal, "USD");

        metodosPagoApi.createPaypalOrder(authHeaderPaypal, body)
                .enqueue(new Callback<PaypalCreateOrderResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PaypalCreateOrderResponse> call,
                                           @NonNull Response<PaypalCreateOrderResponse> response) {

                        toggleConfirmButton(true, false); // restaurar botón

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Error al crear orden PayPal: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        PaypalCreateOrderResponse r = response.body();
                        if (r.getCode() != 1 || r.getData() == null) {
                            String msg = r.getMessage();
                            if (msg == null || msg.trim().isEmpty()) {
                                msg = "No se pudo crear la orden PayPal.";
                            }
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        PaypalCreateOrderResponse.Data data = r.getData();
                        String approvalUrl = data.getApprovalUrl();

                        if (approvalUrl == null || approvalUrl.trim().isEmpty()) {
                            Toast.makeText(requireContext(),
                                    "PayPal no devolvió URL de aprobación.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        abrirPaypalEnNavegador(approvalUrl);

                        Toast.makeText(requireContext(),
                                "Completa el pago en PayPal y regresarás a la app.",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaypalCreateOrderResponse> call,
                                          @NonNull Throwable t) {
                        toggleConfirmButton(true, false);
                        Toast.makeText(requireContext(),
                                "Error de red al crear orden PayPal: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirPaypalEnNavegador(@NonNull String url) {
        try {
            android.content.Intent intent = new android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse(url)
            );
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "No se pudo abrir PayPal.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
