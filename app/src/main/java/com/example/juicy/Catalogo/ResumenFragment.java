package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.juicy.databinding.FragmentResumenBinding;

public class ResumenFragment extends Fragment {

    private FragmentResumenBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResumenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener el Bundle que contiene los datos pasados
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Recuperar los datos del Bundle
            String metodoPago = bundle.getString("metodo_pago", "Método no disponible");
            int idMetodoPago = bundle.getInt("id_metodo_pago", -1);

            // Mostrar los datos en los TextViews correspondientes
            binding.metodoPagoText.setText(metodoPago);
            binding.totalText.setText("s/. 28.00");  // Aquí puedes calcular el total si es necesario.

            // Método de pago guardado en SharedPreferences (si es necesario mostrarlo)
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
            int metodoGuardado = sharedPreferences.getInt("id_metodo_pago", -1);
            if (metodoGuardado != -1) {
                binding.metodoPagoText.setText("Método de pago guardado: " + metodoGuardado);
            }

            // Mostrar los detalles de entrega (ejemplo)
            binding.entregaText.setText("Calle Luis Gonzales, 577");  // Este dato también puede venir de las SharedPreferences o el Bundle
        } else {
            Toast.makeText(requireContext(), "No se recibieron datos.", Toast.LENGTH_SHORT).show();
        }

        // Puedes agregar más lógica aquí para personalizar el comportamiento del resumen
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
