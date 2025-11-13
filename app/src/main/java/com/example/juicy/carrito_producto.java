package com.example.juicy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class carrito_producto extends Fragment {

    private RecyclerView recyclerView;

    public carrito_producto() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_carrito_producto, container, false);

        // Inicializamos las vistas
        recyclerView = view.findViewById(R.id.recyclerCarrito);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // (Por ahora no se cargan productos, solo se muestra la interfaz)
        return view;
    }
}
