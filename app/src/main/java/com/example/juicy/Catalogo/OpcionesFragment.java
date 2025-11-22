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

public class OpcionesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_opciones, container, false);

        Button btnBilletera = root.findViewById(R.id.btnBilletera);
        Button btnDirecciones = root.findViewById(R.id.btnDirecciones);
        Button btnPerfil = root.findViewById(R.id.btnPerfil);

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
}
