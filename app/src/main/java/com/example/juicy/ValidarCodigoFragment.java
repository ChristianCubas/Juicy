package com.example.juicy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.Model.ValidarCodigoRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ValidarCodigoFragment extends Fragment {

    private TextView tvCorreoDestino;
    private EditText etCodigo;
    private Button btnValidar;

    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_validar_codigo, container, false);
        tvCorreoDestino = v.findViewById(R.id.tvCorreoDestino);
        etCodigo = v.findViewById(R.id.etCodigo);
        btnValidar = v.findViewById(R.id.btnValidarCodigo);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email", "");
        }
        if (email == null) email = "";
        tvCorreoDestino.setText("Se envió un código a: " + email);

        btnValidar.setOnClickListener(v -> {
            String codigo = etCodigo.getText().toString().trim();
            if (TextUtils.isEmpty(codigo)) {
                Toast.makeText(getContext(), "Ingrese el código", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(com.example.juicy.network.ApiConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DambJuiceApi api = retrofit.create(DambJuiceApi.class);
            ValidarCodigoRequest req = new ValidarCodigoRequest(email, codigo);

            api.validarCodigo(req).enqueue(new Callback<RptaGeneral>() {
                @Override
                public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RptaGeneral r = response.body();
                    if (r.getCode() == 1) {
                        // MODAL DE ÉXITO
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Código verificado")
                                .setMessage("El código es correcto. Ahora puedes crear una nueva contraseña.")
                                .setPositiveButton("Continuar", (dialog, which) -> {
                                    Bundle b = new Bundle();
                                    b.putString("email", email);

                                    NavHostFragment.findNavController(ValidarCodigoFragment.this)
                                            .navigate(R.id.action_validarCodigoFragment_to_actualizarPasswordFragment, b);
                                })
                                .setCancelable(false)
                                .show();

                    } else {
                        Toast.makeText(getContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RptaGeneral> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
