package com.example.juicy.Model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Model.Compra;
import com.example.juicy.Model.ProductoHistorial;
import com.example.juicy.R;

import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private final Context context;
    private final List<Compra> lista;

    public HistorialAdapter(Context context, List<Compra> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Compra compra = lista.get(position);

        // Llenar datos de cabecera
        holder.tvPedido.setText("Pedido #" + compra.getId_venta());
        holder.tvFecha.setText(compra.getFecha() + "  " + compra.getHora());
        holder.tvTotal.setText(String.format(Locale.getDefault(), "S/ %.2f", compra.getTotal()));

        // --- LÓGICA DINÁMICA PARA PRODUCTOS ---
        // 1. Limpiar el contenedor para evitar duplicados al reciclar la vista
        holder.llProductos.removeAllViews();

        // 2. Verificar si hay productos y agregarlos
        if (compra.getProductos() != null && !compra.getProductos().isEmpty()) {
            for (ProductoHistorial p : compra.getProductos()) {

                // Crear TextView dinámicamente
                TextView tvProd = new TextView(context);

                // Formato: "2x Jugo de Naranja"
                String texto = p.getCantidad() + "x " + p.getNombre();
                tvProd.setText(texto);

                // Estilo
                tvProd.setTextSize(13);
                tvProd.setTextColor(Color.parseColor("#666666")); // Gris oscuro
                tvProd.setPadding(0, 0, 0, 4); // Espacio inferior

                // Agregar al layout contenedor
                holder.llProductos.addView(tvProd);
            }
        } else {
            // Opcional: Mensaje si no hay detalles (aunque no debería pasar)
            TextView tvVacio = new TextView(context);
            tvVacio.setText("Sin detalle");
            tvVacio.setTextSize(12);
            tvVacio.setTextColor(Color.GRAY);
            holder.llProductos.addView(tvVacio);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPedido, tvFecha, tvTotal;
        LinearLayout llProductos; // El contenedor donde inyectamos los productos

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPedido = itemView.findViewById(R.id.tvNumeroPedido);
            tvFecha = itemView.findViewById(R.id.tvFechaHora);
            tvTotal = itemView.findViewById(R.id.tvTotalHistorial);
            // Asegúrate de que este ID exista en tu item_historial.xml
            llProductos = itemView.findViewById(R.id.llContenedorProductos);
        }
    }
}