package com.example.juicy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.juicy.databinding.FragmentCrearCuentaBinding;
import com.example.juicy.databinding.FragmentRecuperarContraseniaBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecuperarContrasenia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecuperarContrasenia extends Fragment {

    public static RecuperarContrasenia newInstance(String param1, String param2) {
        RecuperarContrasenia fragment = new RecuperarContrasenia();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public FragmentRecuperarContraseniaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecuperarContraseniaBinding.inflate(inflater, container, false);

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