package com.example.juicy;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.juicy.databinding.FragmentCrearCuentaBinding;

public class CrearCuenta extends Fragment {
    private FragmentCrearCuentaBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearCuentaBinding.inflate(inflater, container, false);

        binding.btnBack.setOnClickListener(v ->
                        NavHostFragment.findNavController(this).navigateUp()
        );

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
