package com.example.juicy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.juicy.databinding.FragmentCrearCuentaBinding;

public class CrearCuenta extends Fragment {
    public FragmentCrearCuentaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*binding.btnBack.setOnClickListener(v->{
            FragmentManager manager = getActivity().getSupportFragmentManager();
            CrearCuenta fragment_CrearCuenta = new CrearCuenta();
            manager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main,fragment_CrearCuenta)
                    .addToBackStack(null)
                    .commit();
        });*/

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear_cuenta, container, false);
    }
}