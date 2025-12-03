package com.example.juicy.PerfilUsuario;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.juicy.network.VolleySingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.juicy.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilUsuario extends Fragment {

    private TextInputEditText etCorreo, etDni, etNombre, etApellidoP, etApellidoM, etCelular;
    private int idCliente;

    // Variables para guardar los datos actuales
    private String currentNombre, currentApeP, currentApeM, currentDni, currentCelular, currentEmail;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public PerfilUsuario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilUsuario.
     */

    public static PerfilUsuario newInstance(String param1, String param2) {
        PerfilUsuario fragment = new PerfilUsuario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        etCorreo = root.findViewById(R.id.etCorreo);
        etDni = root.findViewById(R.id.etDni);
        etNombre = root.findViewById(R.id.etNombre);
        etApellidoP = root.findViewById(R.id.etApellidoP);
        etApellidoM = root.findViewById(R.id.etApellidoM);
        etCelular = root.findViewById(R.id.etCelular);
        Button btnEditar = root.findViewById(R.id.btnEditarPerfil);
        Button btnHistorial = root.findViewById(R.id.btnHistorial);


        if (btnHistorial != null) {
            btnHistorial.setOnClickListener(v -> {
                NavHostFragment.findNavController(this).navigate(R.id.historialComprasFragment);
            });
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        idCliente = prefs.getInt("idCliente", 0);

        if (idCliente != 0) {
            cargarDatosPerfil();
        }

        if (btnEditar != null) {
            btnEditar.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("nombre", currentNombre);
                args.putString("apePaterno", currentApeP);
                args.putString("apeMaterno", currentApeM);
                args.putString("dni", currentDni);
                args.putString("celular", currentCelular);
                args.putString("email", currentEmail);

                NavHostFragment.findNavController(PerfilUsuario.this)
                        .navigate(R.id.editarPerfil, args);
            });
        }
        setupBottomNavigation(root);

        return root;
    }

    private void cargarDatosPerfil() {
        String URL = com.example.juicy.network.ApiConfig.BASE_URL + "api_obtener_cliente/" + idCliente;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        if (response.getInt("code") == 1) {
                            JSONObject data = response.getJSONObject("data");

                            currentEmail = data.optString("email");
                            currentDni = data.optString("nro_dni");
                            currentNombre = data.optString("nombre");
                            currentApeP = data.optString("ape_paterno");
                            currentApeM = data.optString("ape_materno");
                            currentCelular = data.optString("celular");

                            etCorreo.setText(currentEmail);
                            etDni.setText(currentDni);
                            etNombre.setText(currentNombre);
                            etApellidoP.setText(currentApeP);
                            etApellidoM.setText(currentApeM);
                            etCelular.setText(currentCelular);
                        }
                    } catch (Exception e) {
                        Log.e("Perfil", "Error parsing: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(getContext(), "Error cargando perfil", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    private void setupBottomNavigation(View root) {
        BottomNavigationView bottomNav = root.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;

        bottomNav.setSelectedItemId(R.id.nav_opciones);
        bottomNav.setOnItemSelectedListener(item -> {
            int targetDest;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                targetDest = R.id.homeFragment;
            } else if (id == R.id.nav_carrito) {
                targetDest = R.id.carritoFragment;
            } else if (id == R.id.nav_opciones) {
                targetDest = R.id.opcionesFragment;
            } else {
                return false;
            }

            androidx.navigation.NavController navController = NavHostFragment.findNavController(this);
            androidx.navigation.NavDestination current = navController.getCurrentDestination();
            if (current != null && current.getId() == targetDest) return true;

            navController.navigate(targetDest);
            return true;
        });
    }
}
