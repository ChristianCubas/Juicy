package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.databinding.FragmentFirstBinding;
import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.Model.MeResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        binding.tvRegistrarme.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        binding.btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String email = binding.etCorreo.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showDialog("Datos incompletos", "Ingrese su correo y contraseña.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://grupotres20252.pythonanywhere.com/")
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
                        showDialog("Credenciales incorrectas",
                                "El correo o la contraseña no son válidos. Inténtalo nuevamente.");
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


                    Toast.makeText(getContext(), "Token JWT obtenido", Toast.LENGTH_SHORT).show();


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


                                Toast.makeText(
                                        getContext(),
                                        "Bienvenido, " + nombre + " (ID: " + idCliente + ")",
                                        Toast.LENGTH_LONG
                                ).show();
                            } else {
                                Toast.makeText(getContext(),
                                        "Error al obtener datos del cliente",
                                        Toast.LENGTH_SHORT).show();
                            }


                            NavHostFragment.findNavController(FirstFragment.this)
                                    .navigate(R.id.agregarDirecciones);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
