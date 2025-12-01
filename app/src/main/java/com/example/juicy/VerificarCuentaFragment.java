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
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.Model.VerificacionCodigoRequest;
import com.example.juicy.Model.VerificacionEnviarRequest;
import com.example.juicy.databinding.FragmentVerificarCuentaBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerificarCuentaFragment extends Fragment {

    private FragmentVerificarCuentaBinding binding;
    private String email;
    private String celular;
    private String medioActual = "email";
    private String destinoMascarado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVerificarCuentaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email", "");
            celular = getArguments().getString("celular", "");
            medioActual = getArguments().getString("medio", medioActual);
            destinoMascarado = getArguments().getString("destino_mascarado", "");
        }

        if (binding.toggleMedio != null) {
            binding.toggleMedio.check("sms".equals(medioActual) ? R.id.btnMedioSms : R.id.btnMedioCorreo);
            binding.toggleMedio.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (!isChecked) return;
                medioActual = checkedId == R.id.btnMedioSms ? "sms" : "email";
                actualizarDestino();
            });
        }

        binding.btnReenviar.setOnClickListener(v -> reenviarCodigo());
        binding.btnValidar.setOnClickListener(v -> validarCodigo());

        actualizarDestino();
    }

    private void actualizarDestino() {
        if (binding == null) return;

        boolean esSms = "sms".equals(medioActual);
        String destinoReal = esSms ? celular : email;

        if (TextUtils.isEmpty(destinoReal)) {
            binding.tvDestino.setText(esSms
                    ? "No registraste un número de celular válido."
                    : "No registraste un correo electrónico válido.");
            binding.btnReenviar.setEnabled(false);
            return;
        }

        binding.btnReenviar.setEnabled(true);

        String mascaraLocal = destinoMascarado;
        if (TextUtils.isEmpty(mascaraLocal)) {
            mascaraLocal = esSms ? maskPhone(destinoReal) : maskEmail(destinoReal);
        }
        binding.tvDestino.setText("Enviaremos el código a: " + mascaraLocal);
    }

    private void reenviarCodigo() {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Falta el correo asociado a la cuenta.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean esSms = "sms".equals(medioActual);
        if (esSms && TextUtils.isEmpty(celular)) {
            Toast.makeText(getContext(), "No registraste un número de celular para esta cuenta.", Toast.LENGTH_SHORT).show();
            return;
        }

        DambJuiceApi api = crearApi();
        api.reenviarCodigoVerificacion(new VerificacionEnviarRequest(email, medioActual))
                .enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call, @NonNull Response<RptaGeneral> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(getContext(), "No se pudo reenviar el código", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        RptaGeneral r = response.body();
                        if (r.getCode() == 1) {
                            Map<String, Object> data = toMap(r.getData());
                            String nuevoMedio = safeString(data.get("medio"));
                            if (!TextUtils.isEmpty(nuevoMedio)) {
                                medioActual = nuevoMedio;
                                if (binding.toggleMedio != null) {
                                    binding.toggleMedio.check("sms".equals(medioActual)
                                            ? R.id.btnMedioSms
                                            : R.id.btnMedioCorreo);
                                }
                            }
                            destinoMascarado = safeString(data.get("destino_mascarado"));
                            actualizarDestino();
                            Toast.makeText(getContext(), "Código reenviado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Error al reenviar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validarCodigo() {
        String codigo = binding.etCodigoVerificacion.getText() != null
                ? binding.etCodigoVerificacion.getText().toString().trim()
                : "";
        if (TextUtils.isEmpty(codigo)) {
            Toast.makeText(getContext(), "Ingresa el código enviado", Toast.LENGTH_SHORT).show();
            return;
        }

        DambJuiceApi api = crearApi();
        api.validarCodigoVerificacion(new VerificacionCodigoRequest(email, codigo))
                .enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call, @NonNull Response<RptaGeneral> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(getContext(), "No se pudo validar el código", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RptaGeneral r = response.body();
                        if (r.getCode() == 1) {
                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Cuenta verificada")
                                    .setMessage("Tu cuenta ya está lista. Ahora puedes iniciar sesión.")
                                    .setPositiveButton("Ir a iniciar sesión", (dialog, which) -> {
                                        NavHostFragment.findNavController(VerificarCuentaFragment.this)
                                                .navigate(R.id.action_verificarCuentaFragment_to_FirstFragment);
                                    })
                                    .setCancelable(false)
                                    .show();
                        } else {
                            Toast.makeText(getContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private DambJuiceApi crearApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(com.example.juicy.network.ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(DambJuiceApi.class);
    }

    private Map<String, Object> toMap(Object data) {
        if (data instanceof Map) {
            //noinspection unchecked
            return (Map<String, Object>) data;
        }
        return Collections.emptyMap();
    }

    private String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String maskEmail(String value) {
        if (TextUtils.isEmpty(value) || !value.contains("@")) return value;
        String[] parts = value.split("@");
        String local = parts[0];
        if (local.length() <= 2) {
            return local.charAt(0) + "*@" + parts[1];
        }
        return local.substring(0, 1) + "***" + local.substring(local.length() - 1) + "@" + parts[1];
    }

    private String maskPhone(String value) {
        if (TextUtils.isEmpty(value)) return value;
        if (value.length() <= 3) {
            return "***";
        }
        return "***" + value.substring(value.length() - 3);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
