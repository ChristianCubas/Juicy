package com.example.juicy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.RecuperarRequest;
import com.example.juicy.Model.RptaGeneral;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecuperarCorreoFragment extends Fragment {

    private EditText etCorreo;
    private Button btnEnviar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recuperar_correo, container, false);
        etCorreo = v.findViewById(R.id.etCorreoRecuperar);
        btnEnviar = v.findViewById(R.id.btnEnviarCodigo);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEnviar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            if (TextUtils.isEmpty(correo)) {
                Toast.makeText(getContext(), "Ingrese su correo", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://grupotres20252.pythonanywhere.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DambJuiceApi api = retrofit.create(DambJuiceApi.class);
            RecuperarRequest req = new RecuperarRequest(correo);

            api.enviarCodigo(req).enqueue(new Callback<RptaGeneral>() {
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
                                .setTitle("Correo enviado")
                                .setMessage("Te hemos enviado un código de 4 dígitos a:\n" + correo)
                                .setPositiveButton("Continuar", (dialog, which) -> {
                                    Bundle b = new Bundle();
                                    b.putString("email", correo);

                                    NavHostFragment.findNavController(RecuperarCorreoFragment.this)
                                            .navigate(R.id.action_recuperarCorreoFragment_to_validarCodigoFragment, b);
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
