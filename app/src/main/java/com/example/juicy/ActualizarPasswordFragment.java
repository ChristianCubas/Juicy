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
import com.example.juicy.Model.ActualizarPasswordRequest;
import com.example.juicy.Model.RptaGeneral;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActualizarPasswordFragment extends Fragment {

    private TextView tvCorreoActualiza;
    private EditText etNuevaPass, etConfirmarPass;
    private Button btnActualizar;

    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_actualizar_password, container, false);
        tvCorreoActualiza = v.findViewById(R.id.tvCorreoActualiza);
        etNuevaPass = v.findViewById(R.id.etNuevaPass);
        etConfirmarPass = v.findViewById(R.id.etConfirmarPass);
        btnActualizar = v.findViewById(R.id.btnActualizarPass);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email", "");
        }
        if (email == null) email = "";
        tvCorreoActualiza.setText("Actualizando para: " + email);

        btnActualizar.setOnClickListener(v -> {
            String pass1 = etNuevaPass.getText().toString().trim();
            String pass2 = etConfirmarPass.getText().toString().trim();

            if (TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)) {
                Toast.makeText(getContext(), "Ingrese y confirme la contraseña", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass1.equals(pass2)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://grupotres20252.pythonanywhere.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DambJuiceApi api = retrofit.create(DambJuiceApi.class);
            ActualizarPasswordRequest req = new ActualizarPasswordRequest(email, pass1);

            api.actualizarPassword(req).enqueue(new Callback<RptaGeneral>() {
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
                                .setTitle("Contraseña actualizada")
                                .setMessage("Tu contraseña se actualizó correctamente. Ahora puedes iniciar sesión.")
                                .setPositiveButton("Ir al inicio de sesión", (dialog, which) -> {
                                    NavHostFragment.findNavController(ActualizarPasswordFragment.this)
                                            .navigate(R.id.FirstFragment);
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
