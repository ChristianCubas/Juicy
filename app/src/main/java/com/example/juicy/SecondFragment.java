package com.example.juicy;

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

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.databinding.FragmentSecondBinding;
import com.example.juicy.Model.RegistrarClienteRequest;
import com.example.juicy.Model.RptaGeneral;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.registerButton.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String nombres = safe(binding.names.getText());
        String apellidos = safe(binding.surnames.getText());
        String dni = safe(binding.dni.getText());
        String celular = safe(binding.cellphone.getText());
        String email = safe(binding.email.getText());
        String pass = safe(binding.password.getText());
        String pass2 = safe(binding.confirmPassword.getText());

        if (TextUtils.isEmpty(nombres) || TextUtils.isEmpty(apellidos) ||
                TextUtils.isEmpty(dni) || TextUtils.isEmpty(celular) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dni.length() != 8) {
            Toast.makeText(getContext(), "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show(); return;
        }
        if (celular.length() != 9) {
            Toast.makeText(getContext(), "El celular debe tener 9 dígitos", Toast.LENGTH_SHORT).show(); return;
        }
        if (!pass.equals(pass2)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show(); return;
        }


        String apePaterno = apellidos.trim();
        String apeMaterno = "";
        if (apellidos.contains(" ")) {
            int idx = apellidos.indexOf(" ");
            apePaterno = apellidos.substring(0, idx).trim();
            apeMaterno = apellidos.substring(idx + 1).trim();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(com.example.juicy.network.ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DambJuiceApi api = retrofit.create(DambJuiceApi.class);

        RegistrarClienteRequest req = new RegistrarClienteRequest();
        req.setEmail(email);
        req.setPassword(pass);
        req.setNro_dni(dni);
        req.setNombre(nombres);
        req.setApe_paterno(apePaterno);
        req.setApe_materno(apeMaterno);
        req.setCelular(celular);

        api.registrarCliente(req).enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RptaGeneral r = response.body();
                if (r == null) {
                    Toast.makeText(getContext(), "Respuesta vacía", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (r.getCode() == 1) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Registro")
                            .setMessage("¡Registrado correctamente!")
                            .setPositiveButton("OK", (dialog, which) -> {

                                NavHostFragment.findNavController(SecondFragment.this)
                                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(getContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(getContext(), "Conexión fallida: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
