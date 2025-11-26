package com.example.juicy.Catalogo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OpcionesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_opciones, container, false);

        Button btnBilletera = root.findViewById(R.id.btnBilletera);
        Button btnDirecciones = root.findViewById(R.id.btnDirecciones);
        Button btnPerfil = root.findViewById(R.id.btnPerfil);
        setupBottomNavigation(root);

        btnBilletera.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.paymentWalletFragment));

        btnDirecciones.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.direccionesFragment));

        btnPerfil.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.perfilUsuario));

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
