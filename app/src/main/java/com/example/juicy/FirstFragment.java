package com.example.juicy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.ronaldApi;
import com.example.juicy.Model.AuthRequest;
import com.example.juicy.Model.AuthResponse;
import com.example.juicy.databinding.FragmentFirstBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    /* Hola soy Medalith, probando commits en github
     * prueba 02 */

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvRegistrarme.setOnClickListener(v -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        binding.btnIniciarSesion.setOnClickListener(v -> {
            String correo = binding.etCorreo.getText() != null ? binding.etCorreo.getText().toString() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            getToken(correo, password);
        });


        /*binding.tvOlvidePassword.setOnClickListener(v->{
            NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_RecuperarContrasenia);
        });*/
    }
    private void getToken(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://grupotres20252.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ronaldApi api = retrofit.create(ronaldApi.class);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);

        Call<AuthResponse> call = api.obtenerToken(authRequest);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthResponse authResponse = response.body();
                if (authResponse != null && authResponse.getAccess_token() != null) {
                    // Guardamos datos en SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("SP_USAT", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("tokenJWT", authResponse.getAccess_token());
                    editor.apply();

                    // Navegar a la siguiente pantalla (Billetera o MainActivity)
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_BilleteraMetodoPagoFragment);
                } else {
                    Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isPasswordValid(String text) {
        return text != null && text.length() >= 5;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}