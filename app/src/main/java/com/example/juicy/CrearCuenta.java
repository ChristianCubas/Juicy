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
        binding = FragmentCrearCuentaBinding.inflate(inflater, container, false);

        binding.btnBack.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FirstFragment fragment_login = new FirstFragment();
            manager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, fragment_login)
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
    }
}