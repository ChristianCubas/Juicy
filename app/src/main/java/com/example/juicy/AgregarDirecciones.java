package com.example.juicy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class AgregarDirecciones extends Fragment {

    private EditText etCategoria, etTipoVia, etNombreVia, etNumeroVia, etDepartamento, etReferencia, etCiudad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_agregar_direcciones, container, false);

        etCategoria = view.findViewById(R.id.etCategoria);
        etTipoVia = view.findViewById(R.id.etTipoVia);
        etNombreVia = view.findViewById(R.id.etNombreVia);
        etNumeroVia = view.findViewById(R.id.etNumeroVia);
        etDepartamento = view.findViewById(R.id.etDepartamento);
        etReferencia = view.findViewById(R.id.etReferencia);
        etCiudad = view.findViewById(R.id.etCiudad);
        View btnGuardar = view.findViewById(R.id.btnGuardarDireccion);
        View btnAbrirMapa = view.findViewById(R.id.btnAbrirMapa);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Dirección guardada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
        });

        btnAbrirMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Abriendo mapa para seleccionar dirección", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AgregarDirecciones.this)
                        .navigate(R.id.buscarMapa);
            }
        });

        return view;
    }

    private void limpiarCampos() {
        etCategoria.setText("");
        etTipoVia.setText("");
        etNombreVia.setText("");
        etNumeroVia.setText("");
        etDepartamento.setText("");
        etReferencia.setText("");
        etCiudad.setText("");
    }
}