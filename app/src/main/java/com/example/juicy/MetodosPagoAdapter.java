package com.example.juicy.Catalogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.R;

import java.util.List;

public class MetodosPagoAdapter extends RecyclerView.Adapter<MetodosPagoAdapter.MetodoPagoViewHolder> {

    private List<MetodoPago> metodoPagoList;
    private Context context;
    private int selectedPosition = -1;  // Variable para almacenar la posición del RadioButton seleccionado

    public MetodosPagoAdapter(List<MetodoPago> metodoPagoList, Context context) {
        this.metodoPagoList = metodoPagoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MetodoPagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tpago, parent, false);
        return new MetodoPagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MetodoPagoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MetodoPago metodoPago = metodoPagoList.get(position);

        // Asignar los valores del método de pago
        holder.cardNumber.setText(metodoPago.getNumTarjeta());
        holder.cardHolder.setText(metodoPago.getTitular());
        holder.cardExp.setText(metodoPago.getFechaExpiracion());

        // Establecer si el RadioButton está marcado o no
        holder.radioPrincipal.setChecked(position == selectedPosition);

        // Listener para manejar el estado de selección
        // En tu adaptador, cuando un RadioButton es seleccionado
        holder.radioPrincipal.setOnClickListener(v -> {
            selectedPosition = position;
            // Guardar el método de pago seleccionado en SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("metodoPagoSeleccionado", metodoPago.getIdMetodoPago());  // Guarda el ID del método de pago
            editor.apply();  // Asegúrate de guardar
            notifyDataSetChanged();  // Actualiza el RecyclerView
        });
    }

    @Override
    public int getItemCount() {
        return metodoPagoList.size();
    }

    public static class MetodoPagoViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber, cardHolder, cardExp;
        RadioButton radioPrincipal;  // Cambié CheckBox por RadioButton

        public MetodoPagoViewHolder(View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.idNumeroTarjeta1);
            cardHolder = itemView.findViewById(R.id.primaryCardHolder);
            cardExp = itemView.findViewById(R.id.primaryCardExp);
            radioPrincipal = itemView.findViewById(R.id.radioPrincipal);  // Cambié CheckBox por RadioButton
        }
    }
}
