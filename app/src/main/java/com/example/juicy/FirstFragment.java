package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.databinding.FragmentFirstBinding;
import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.Model.MeResponse;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.Model.VerificacionEstadoRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        binding.btnIniciarSesion.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });
*/
        binding.btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        binding.tvOlvidePassword.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_recuperarCorreoFragment);
        });
        binding.tvRegistrarme.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        runEntranceAnimations();
    }

    private void iniciarSesion() {
        String email = binding.etCorreo.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showDialog("Datos incompletos", "Ingrese su correo y contraseña.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(com.example.juicy.network.ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DambJuiceApi api = retrofit.create(DambJuiceApi.class);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(email);
        authRequest.setPassword(password);

        api.obtenerToken(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (!response.isSuccessful()) {

                    if (response.code() == 401 || response.code() == 400) {
                        revisarEstadoCuenta(api, email, () ->
                                showDialog("Credenciales incorrectas",
                                        "El correo o la contraseña no son válidos. Inténtalo nuevamente."));
                    } else {
                        showDialog("No se pudo iniciar sesión",
                                "Código de error: " + response.code());
                    }
                    return;
                }

                AuthResponse authResponse = response.body();
                if (authResponse != null && authResponse.getAccess_token() != null) {
                    String token = authResponse.getAccess_token();


                    SharedPreferences prefs = requireActivity()
                            .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                    prefs.edit().putString("tokenJWT", token).apply();




                    String authHeader = "JWT " + token;
                    api.me(authHeader).enqueue(new Callback<MeResponse>() {
                        @Override
                        public void onResponse(Call<MeResponse> call, Response<MeResponse> resp) {
                            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 1) {
                                MeResponse me = resp.body();
                                int idCliente = me.getId_cliente();
                                String nombre = me.getNombre();


                                prefs.edit()
                                        .putInt("idCliente", idCliente)
                                        .putString("nombreCliente", nombre)
                                        .apply();




                                NavHostFragment.findNavController(FirstFragment.this)
                                        .navigate(R.id.action_FirstFragment_to_HomeFragment);
                            } else {
                                Toast.makeText(getContext(),
                                        "Error al obtener datos del cliente",
                                        Toast.LENGTH_SHORT).show();
                            }

/*
                            NavHostFragment.findNavController(FirstFragment.this)
                                    .navigate(R.id.agregarDirecciones);
*/

                        }

                        @Override
                        public void onFailure(Call<MeResponse> call, Throwable t) {
                            Toast.makeText(getContext(),
                                    "Error de conexión: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    showDialog("Respuesta inválida",
                            "No se recibió el token de autenticación.");
                }

            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showDialog("Error de conexión",
                        "No fue posible conectarse con el servidor.\n" + t.getMessage());
            }
        });
    }

    private void showDialog(String title, String message) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setCancelable(true)
                .show();
    }

    private void revisarEstadoCuenta(DambJuiceApi api, String email, Runnable fallback) {
        if (TextUtils.isEmpty(email)) {
            if (fallback != null) fallback.run();
            return;
        }
        api.verificarEstadoCuenta(new VerificacionEstadoRequest(email))
                .enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call, @NonNull Response<RptaGeneral> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            if (fallback != null) fallback.run();
                            return;
                        }
                        RptaGeneral r = response.body();
                        if (r.getCode() != 1) {
                            if (fallback != null) fallback.run();
                            return;
                        }
                        Map<String, Object> data = toMap(r.getData());
                        boolean needsVerification = getBoolean(data.get("needs_verification"));
                        if (needsVerification) {
                            String celular = safeString(data.get("celular"));
                            String medio = safeString(data.get("medio"));
                            String destino = safeString(data.get("destino_mascarado"));
                            mostrarDialogoVerificacion(email, celular, medio, destino);
                        } else if (fallback != null) {
                            fallback.run();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call, @NonNull Throwable t) {
                        if (fallback != null) fallback.run();
                    }
                });
    }

    private void mostrarDialogoVerificacion(String email, String celular, String medio, String destinoMasc) {
        if (getContext() == null) return;
        String medioLimpio = TextUtils.isEmpty(medio) ? "email" : medio;
        String destinoMostrado = TextUtils.isEmpty(destinoMasc)
                ? ("sms".equals(medioLimpio) ? maskPhone(celular) : maskEmail(email))
                : destinoMasc;

        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("celular", celular);
        args.putString("medio", medioLimpio);
        args.putString("destino_mascarado", destinoMostrado);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cuenta pendiente de verificación")
                .setMessage("Tu cuenta aún no está verificada. Ingresa el código enviado a " + destinoMostrado + ".")
                .setPositiveButton("Ingresar código", (dialog, which) ->
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_verificarCuentaFragment, args))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private Map<String, Object> toMap(Object data) {
        if (data instanceof Map) {
            //noinspection unchecked
            return (Map<String, Object>) data;
        }
        return Collections.emptyMap();
    }

    private boolean getBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }

    private String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String maskEmail(String email) {
        if (TextUtils.isEmpty(email) || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String local = parts[0];
        if (local.length() <= 2) {
            return local.charAt(0) + "*@" + parts[1];
        }
        return local.substring(0, 1) + "***" + local.substring(local.length() - 1) + "@" + parts[1];
    }

    private String maskPhone(String celular) {
        if (TextUtils.isEmpty(celular) || celular.length() < 3) return celular;
        return "***" + celular.substring(celular.length() - 3);
    }

    private void runEntranceAnimations() {
        if (binding == null) return;

        // Logo pop (imagen)
        binding.imgLogo.setScaleX(0.7f);
        binding.imgLogo.setScaleY(0.7f);
        binding.imgLogo.setAlpha(0f);
        binding.imgLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600L)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        View[] toAnimate = new View[]{
                binding.tvBienvenido,
                binding.tilCorreo,
                binding.tilPassword,
                binding.tvOlvidePassword,
                binding.btnIniciarSesion,
                binding.tvRegistrarme
        };
        long delay = 150L;
        for (int i = 0; i < toAnimate.length; i++) {
            View v = toAnimate[i];
            if (v == null) continue;
            v.setAlpha(0f);
            v.setTranslationY(30f);
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(delay + i * 70L)
                    .setDuration(400L)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
