package com.example.juicy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.juicy.R;
import com.example.juicy.MetodoPagoEntry;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MetodosPagoAdapter extends RecyclerView.Adapter<MetodosPagoAdapter.ViewHolder> {

    // Acciones por ítem: seleccionar (opcional) y solicitar borrado
    public interface OnItemActionListener {
        void onRequestDelete(@NonNull MetodoPagoEntry metodo);
        void onSelect(@NonNull MetodoPagoEntry metodo);
    }
    private final Context context;
    private final OnItemActionListener listener;

    private final List<MetodoPagoEntry> items = new ArrayList<>();
    private Integer selectedId = null; // para resaltar un ítem (opcional)

    public MetodosPagoAdapter(@NonNull Context context,
                              @NonNull OnItemActionListener listener) {
        this.context = context;
        this.listener = listener;
    }



    // Reemplaza el dataset completo
    public void setData(List<MetodoPagoEntry> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public void setSelectedId(Integer id) {
        this.selectedId = id;
        notifyDataSetChanged();
    }

    public MetodoPagoEntry getItem(int position) {
        return (position >= 0 && position < items.size()) ? items.get(position) : null;
    }

    @NonNull
    @Override
    public MetodosPagoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_metodo_pago, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MetodosPagoAdapter.ViewHolder h, int position) {
        MetodoPagoEntry m = items.get(position);

        h.txtNumero.setText(m.getNum_tarjeta_mask());
        h.txtTitular.setText(m.getTitular());
        h.txtExp.setText("Vence " + m.getFecha_expiracion());

        // Resaltado opcional del seleccionado con stroke
        MaterialCardView card = (MaterialCardView) h.itemView;
        int stroke = h.itemView.getResources().getDimensionPixelSize(R.dimen.payment_card_stroke);
        boolean isSelected = (selectedId != null && selectedId == m.getId_metodo_pago());
        card.setStrokeWidth(isSelected ? stroke : 0);

        // Click en toda la tarjeta (selección)
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSelect(m);
        });

        // Click en el icono de basurero (solicitar borrado)
        h.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onRequestDelete(m);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumero, txtTitular, txtExp;
        ImageButton btnEliminar;

        ViewHolder(@NonNull View v) {
            super(v);
            txtNumero  = v.findViewById(R.id.txtnum);
            txtTitular = v.findViewById(R.id.txtTit);
            txtExp     = v.findViewById(R.id.txtex);
            btnEliminar = v.findViewById(R.id.btnEli);
        }
    }


}