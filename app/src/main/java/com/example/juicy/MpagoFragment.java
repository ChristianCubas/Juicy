package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.juicy.Interface.metodosPagoApi;
import com.example.juicy.Model.MetodoPagoVentaRequest;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.databinding.FragmentMpagoBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MpagoFragment extends Fragment {
    private metodosPagoApi api;
    private String authHeader;
    private int idCliente;
    private Integer idMetodoPrimario;
    private Integer idMetodoSecundario;
    private enum SelectedMethod {
        SAVED_PRIMARY,
        SAVED_SECONDARY,
        OTHER_VISA,
        OTHER_PAYPAL
    }

    private FragmentMpagoBinding binding;
    private SelectedMethod selectedMethod = SelectedMethod.SAVED_PRIMARY;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMpagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        updateSelectionUI();

        // Token + id_cliente desde SP (como en tus otros fragments)
        SharedPreferences sp = requireActivity().getSharedPreferences("SP_USAT", Context.MODE_PRIVATE);
        String token = sp.getString("tokenJWT", "");
        idCliente = sp.getInt("id_cliente", 0);
        authHeader = "JWT " + (token == null ? "" : token.trim());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://grupotres20252.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(metodosPagoApi.class);

        // Continuar
        binding.continueButton.setOnClickListener(v -> onContinuar());
    }

    private void onContinuar() {
        Integer idMetodo = resolveMetodoSeleccionado();
        if (idMetodo == null || idMetodo <= 0) {
            Toast.makeText(requireContext(), "Seleccione un método válido", Toast.LENGTH_SHORT).show();
            return;
        }
        MetodoPagoVentaRequest body = new MetodoPagoVentaRequest();
        body.setId_cliente(idCliente);
        body.setId_metodo_pago(idMetodo);

        api.setMetodoPagoVenta(authHeader, body).enqueue(new Callback<RptaGeneral>() {
            @Override public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(), "Código: " + resp.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RptaGeneral r = resp.body();
                Toast.makeText(requireContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                if (r.getCode() == 1) {
                    // continuar flujo (navegar a confirmar/checkout, etc.)
                }
            }
            @Override public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Traduce la selección visual al id_metodo_pago
    @Nullable
    private Integer resolveMetodoSeleccionado() {
        switch (selectedMethod) {
            case SAVED_PRIMARY:   return idMetodoPrimario;
            case SAVED_SECONDARY: return idMetodoSecundario;
            case OTHER_VISA:
            case OTHER_PAYPAL:
                // Aquí no crees métodos nuevos: redirige a Agregar
                Toast.makeText(requireContext(),
                        "Agregue el método primero en 'Agregar tarjeta'", Toast.LENGTH_SHORT).show();
                return null;
            default: return null;
        }
    }

    private void setupListeners() {
        binding.primaryCard.setOnClickListener(v -> selectMethod(SelectedMethod.SAVED_PRIMARY));
        binding.secondaryCard.setOnClickListener(v -> selectMethod(SelectedMethod.SAVED_SECONDARY));
        binding.otherVisaCard.setOnClickListener(v -> selectMethod(SelectedMethod.OTHER_VISA));
        binding.otherPaypalCard.setOnClickListener(v -> selectMethod(SelectedMethod.OTHER_PAYPAL));
    }

    private void selectMethod(@NonNull SelectedMethod method) {
        if (selectedMethod != method) {
            selectedMethod = method;
            updateSelectionUI();
        }
    }

    private void updateSelectionUI() {
        if (binding == null) {
            return;
        }
        int checkedIcon = R.drawable.ic_check_circle_file;
        int uncheckedIcon = R.drawable.ic_uncheck_circle;

        binding.primaryCardStatus.setImageResource(
                selectedMethod == SelectedMethod.SAVED_PRIMARY ? checkedIcon : uncheckedIcon);
        binding.secondaryCardStatus.setImageResource(
                selectedMethod == SelectedMethod.SAVED_SECONDARY ? checkedIcon : uncheckedIcon);
        binding.otherVisaStatus.setImageResource(
                selectedMethod == SelectedMethod.OTHER_VISA ? checkedIcon : uncheckedIcon);
        binding.otherPaypalStatus.setImageResource(
                selectedMethod == SelectedMethod.OTHER_PAYPAL ? checkedIcon : uncheckedIcon);

        binding.otherVisaFields.setVisibility(
                selectedMethod == SelectedMethod.OTHER_VISA ? View.VISIBLE : View.GONE);
        binding.otherPaypalFields.setVisibility(
                selectedMethod == SelectedMethod.OTHER_PAYPAL ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}