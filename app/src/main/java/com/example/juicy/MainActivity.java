package com.example.juicy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.juicy.Interface.metodosPagoApi;
import com.example.juicy.Model.PaypalCaptureRequest;
import com.example.juicy.Model.PaypalCaptureResponse;
import com.example.juicy.databinding.ActivityMainBinding;
import com.example.juicy.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    // 🔹 PayPal / API
    private metodosPagoApi api;
    private String authHeader;
    private int idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cambia del tema de splash al tema principal al iniciar
        setTheme(R.style.Theme_Juicy);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Fade-in del contenido tras el splash
        binding.getRoot().setAlpha(0f);
        binding.getRoot().animate()
                .alpha(1f)
                .setDuration(450L)
                .setStartDelay(150L)
                .start();

        // El id debe coincidir con el XML: nav_host_fragment
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("No se encontró nav_host_fragment en activity_main.xml");
        }

        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Inicializar auth + Retrofit
        setupApiAndAuth();

        // Manejar posible deep link de PayPal (si la app se abrió desde el navegador)
        handlePaypalDeepLink(getIntent());
    }

    // Si la Activity ya estaba abierta y vuelve desde el navegador con un nuevo Intent
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlePaypalDeepLink(intent);
    }

    private void setupApiAndAuth() {
        SharedPreferences sp = getSharedPreferences("SP_JUICY", MODE_PRIVATE);
        String token = sp.getString("tokenJWT", "");
        idCliente = sp.getInt("idCliente", 0);

        if (token != null && !token.trim().isEmpty() && idCliente > 0) {
            authHeader = "JWT " + token.trim();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(metodosPagoApi.class);
        } else {
            authHeader = null;
        }
    }

    // Maneja juicy://paypal-return y juicy://paypal-cancel
    private void handlePaypalDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data == null) return;  // app abierta normal

        String host = data.getHost();  // "paypal-return" o "paypal-cancel"

        if ("paypal-cancel".equals(host)) {
            Toast.makeText(this, "Pago cancelado en PayPal.", Toast.LENGTH_LONG).show();
            navegarAPaymentMethod();
            return;
        }

        if (!"paypal-return".equals(host)) {
            return;
        }

        // En Checkout v2, PayPal manda ?token=ORDER_ID
        String orderId = data.getQueryParameter("token");
        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "Orden PayPal sin token.", Toast.LENGTH_LONG).show();
            navegarAPaymentMethod();
            return;
        }

        // Capturar la orden en tu backend
        capturarOrdenPaypal(orderId);
    }

    private void capturarOrdenPaypal(String orderId) {
        if (api == null || authHeader == null || idCliente <= 0) {
            Toast.makeText(this, "Sesión inválida. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show();
            return;
        }

        PaypalCaptureRequest body = new PaypalCaptureRequest(idCliente, orderId);

        api.capturePaypalOrder(authHeader, body).enqueue(new Callback<PaypalCaptureResponse>() {
            @Override
            public void onResponse(Call<PaypalCaptureResponse> call, Response<PaypalCaptureResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainActivity.this,
                            "Error al confirmar pago PayPal: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    navegarAPaymentMethod();
                    return;
                }

                PaypalCaptureResponse r = response.body();
                if (r.getCode() != 1 || r.getData() == null) {
                    String msg = r.getMessage();
                    if (msg == null || msg.trim().isEmpty()) {
                        msg = "No se pudo completar el pago con PayPal.";
                    }
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    navegarAPaymentMethod();
                    return;
                }

                int idMetodoPago = r.getData().getId_metodo_pago();

                // Guardar en SP para usarlo en ResumenFragment
                getSharedPreferences("SP_JUICY", MODE_PRIVATE)
                        .edit()
                        .putInt("idMetodoPagoSeleccionado", idMetodoPago)
                        .apply();

                Toast.makeText(MainActivity.this,
                        "Pago PayPal añadido.",
                        Toast.LENGTH_LONG).show();

                navegarAResumenDespuesPaypal();
            }

            @Override
            public void onFailure(Call<PaypalCaptureResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error de red al confirmar pago PayPal.",
                        Toast.LENGTH_LONG).show();
                navegarAPaymentMethod();
            }
        });
    }

    private void navegarAResumenDespuesPaypal() {
        if (navController == null) {
            NavHostFragment navHostFragment = (NavHostFragment)
                    getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();
            }
        }
        if (navController == null) return;

        Bundle args = new Bundle();
        args.putString("metodo_pago", "PayPal");
        args.putBoolean("paypal_pagado", true); // ✅ señal para el Resumen

        navController.navigate(R.id.resumenFragment, args);
    }


    private void navegarAPaymentMethod() {
        if (navController == null) {
            NavHostFragment navHostFragment = (NavHostFragment)
                    getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();
            }
        }
        if (navController == null) return;

        navController.navigate(R.id.paymentMethodFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        int idActual = navControler.getCurrentDestination().getId();
//        if (idActual == R.id.FirstFragment) {
//            return false;
//        }
        getMenuInflater().inflate(R.menu.menu_inferior, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
