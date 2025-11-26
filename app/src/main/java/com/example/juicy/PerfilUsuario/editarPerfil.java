package com.example.juicy.PerfilUsuario;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.juicy.R;
import com.example.juicy.network.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link editarPerfil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class editarPerfil extends Fragment {

    private TextInputEditText etCorreo, etDni, etNombre, etApellidoP, etApellidoM, etCelular;
    private Button btnGuardar;
    private int idCliente = 0;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public editarPerfil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        etCorreo = root.findViewById(R.id.etCorreo);
        etDni = root.findViewById(R.id.etDni);
        etNombre = root.findViewById(R.id.etNombre);
        etApellidoP = root.findViewById(R.id.etApellidoP);
        etApellidoM = root.findViewById(R.id.etApellidoM);
        etCelular = root.findViewById(R.id.etCelular);
        btnGuardar = root.findViewById(R.id.btnGuardar);

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        idCliente = prefs.getInt("idCliente", 0);

        if (getArguments() != null) {
            etCorreo.setText(getArguments().getString("email"));
            etDni.setText(getArguments().getString("dni"));
            etNombre.setText(getArguments().getString("nombre"));
            etApellidoP.setText(getArguments().getString("apePaterno"));
            etApellidoM.setText(getArguments().getString("apeMaterno"));
            etCelular.setText(getArguments().getString("celular"));

            etCorreo.setEnabled(false);
            etDni.setEnabled(false);
        }

        // 3. Botón Guardar (Lógica pendiente para el siguiente paso)
        btnGuardar.setOnClickListener(v -> {
            guardarCambios();
        });

        return root;
    }

    private void guardarCambios() {
        if (idCliente == 0) {
            Toast.makeText(getContext(), "Error: No se detectó el usuario (ID=0). Por favor inicie sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevoApeP = etApellidoP.getText().toString().trim();
        String nuevoApeM = etApellidoM.getText().toString().trim();
        String nuevoCelular = etCelular.getText().toString().trim();

        if (nuevoNombre.isEmpty() || nuevoApeP.isEmpty() || nuevoApeM.isEmpty() || nuevoCelular.isEmpty()) {
            Toast.makeText(getContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Preparar JSON
        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", idCliente);
            body.put("nombre", nuevoNombre);
            body.put("ape_paterno", nuevoApeP);
            body.put("ape_materno", nuevoApeM);
            body.put("celular", nuevoCelular);
        } catch (Exception e) { e.printStackTrace(); }

        String URL = "https://grupotres20252.pythonanywhere.com/api_actualizar_cliente";

        // 4. Enviar Petición
        btnGuardar.setEnabled(false); // Evitar doble clic
        btnGuardar.setText("Guardando...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, body,
                response -> {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar cambios");
                    try {
                        if (response.getInt("code") == 1) {
                            Toast.makeText(getContext(), "¡Datos actualizados!", Toast.LENGTH_SHORT).show();

                            // para regresar a la otra pantalla
                            NavHostFragment.findNavController(this).popBackStack();

                        } else {
                            Toast.makeText(getContext(), "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error en respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar cambios");
                    Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }
}