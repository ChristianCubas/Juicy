package com.example.juicy;

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

import com.example.juicy.Interface.metodosPagoApi;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.Model.GuardarMetodoPagoRequest;
import com.example.juicy.databinding.FragmentAgregarMetodoPagoBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgregarMetodoPagoFragment extends Fragment {

    private FragmentAgregarMetodoPagoBinding binding;

    private metodosPagoApi api;
    private String authHeader;
    private int idCliente;
    private static final String BASE_URL = "https://grupotres20252.pythonanywhere.com/";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAgregarMetodoPagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = requireActivity().getSharedPreferences("SP_USAT", Context.MODE_PRIVATE);
        String token = sp.getString("tokenJWT", "");
        idCliente = sp.getInt("id_cliente", 0);

        if (token == null || token.trim().isEmpty() || idCliente <= 0) {
            toast("Debe autenticarse");
            return;
        }
        authHeader = "JWT " + token.trim();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(metodosPagoApi.class);

        binding.saveButton.setOnClickListener(v -> guardar());
    }
    private void guardar() {
        String pan = textOf(binding.cardNumberInput);
        String nombre = textOf(binding.nameInput);
        String apellido = textOf(binding.surnameInput);
        String exp = textOf(binding.expirationInput);
        String cvv = textOf(binding.cvvInput);

        // Normalizar y validar rápido
        pan = pan.replaceAll("\\s", "");
        if (TextUtils.isEmpty(pan) || pan.length() < 13) { toast("Número de tarjeta inválido"); return; }
        if (TextUtils.isEmpty(nombre)) { toast("Ingrese el nombre del titular"); return; }
        if (TextUtils.isEmpty(apellido)) { toast("Ingrese el apellido del titular"); return; }
        if (TextUtils.isEmpty(exp) || !exp.matches("^(0[1-9]|1[0-2])\\/\\d{2}$")) { toast("Fecha de expiración inválida (MM/YY)"); return; }
        if (TextUtils.isEmpty(cvv) || cvv.length() < 3) { toast("CVV inválido"); return; }

        // Armar body (clase separada)
        GuardarMetodoPagoRequest body = new GuardarMetodoPagoRequest();
        body.setId_cliente(idCliente);
        body.setTitular((nombre + " " + apellido).trim());
        body.setNum_tarjeta(pan);
        body.setFecha_expiracion(exp);
        body.setCvv(cvv);
        body.setCod_paypal(null); // si no usas PayPal

        api.guardarMetodo(authHeader, body).enqueue(new Callback<RptaGeneral>() {
            @Override public void onResponse(@NonNull Call<RptaGeneral> call, @NonNull Response<RptaGeneral> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    toast("Código: " + resp.code());
                    return;
                }
                RptaGeneral r = resp.body();
                toast(r.getMessage());
                if (r.getCode() == 1) {
                    // Volver a la lista; ésta se refresca en onResume() del fragment anterior
                    NavHostFragment.findNavController(AgregarMetodoPagoFragment.this).popBackStack();
                }
            }
            @Override public void onFailure(@NonNull Call<RptaGeneral> call, @NonNull Throwable t) {
                toast("Error de red");
            }
        });
    }
    private String textOf(@NonNull com.google.android.material.textfield.TextInputLayout til) {
        if (til.getEditText() == null) return "";
        return String.valueOf(til.getEditText().getText()).trim();
    }

    private void toast(String m) {
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}