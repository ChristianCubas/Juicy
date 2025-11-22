package com.example.juicy.PerfilUsuario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.juicy.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilUsuario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilUsuario extends Fragment {


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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        setupBottomNavigation(root);
        Button btnEditar = root.findViewById(R.id.btnEditarPerfil);

        if (btnEditar != null) {
            btnEditar.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Abriendo edición de perfil...", Toast.LENGTH_SHORT).show();

                // Navegar al fragmento editarPerfil usando NavController
                NavHostFragment.findNavController(PerfilUsuario.this)
                        .navigate(R.id.editarPerfil);
            });
        }
        return root;
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
