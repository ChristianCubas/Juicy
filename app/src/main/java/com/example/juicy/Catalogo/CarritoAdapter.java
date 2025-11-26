package com.example.juicy.Catalogo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Model.CarritoItem;
import com.example.juicy.R;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private static final String TAG = "CarritoAdapter"; // Para logs

    private final Context context;
    private final List<CarritoItem> lista;
    private final OnCarritoChangeListener changeListener;

    public CarritoAdapter(Context context, List<CarritoItem> lista,
                          OnCarritoChangeListener changeListener) {
        this.context = context;
        this.lista = lista;
        this.changeListener = changeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarritoItem item = lista.get(position);

        Log.d(TAG, "Binding pos " + position + ": " + item.getNombreProducto() + " - ID: " + item.getIdDetalle());


        holder.tvTipo.setText(item.getDetallePersonalizacion());
        holder.tvNombre.setText(item.getNombreProducto());
        holder.tvCantidad.setText(String.format("%02d", item.getCantidad()));
        holder.tvPrecio.setText(String.format("S/ %.2f", item.getPrecioTotal()));

        holder.btnMas.setOnClickListener(v -> incrementarCantidad(holder.getBindingAdapterPosition()));
        holder.btnMenos.setOnClickListener(v -> decrementarCantidad(holder.getBindingAdapterPosition()));

        holder.btnEliminar.setOnClickListener(v -> {
            int idParaBorrar = item.getIdDetalle();
            Log.d(TAG, "Click Eliminar en pos " + holder.getBindingAdapterPosition() + ". ID a borrar: " + idParaBorrar);

            if (changeListener != null) {
                changeListener.onEliminarItem(idParaBorrar, holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private void incrementarCantidad(int position) {
        if (position == RecyclerView.NO_POSITION) return;
        CarritoItem item = lista.get(position);

        double precioUnitario = item.getCantidad() > 0
                ? item.getPrecioTotal() / item.getCantidad()
                : 0;

        item.setCantidad(item.getCantidad() + 1);
        item.setPrecioTotal(precioUnitario * item.getCantidad());

        notifyItemChanged(position);
        notificarTotal();
    }

    private void decrementarCantidad(int position) {
        if (position == RecyclerView.NO_POSITION) return;
        CarritoItem item = lista.get(position);
        if (item.getCantidad() <= 1) {
            Toast.makeText(context, "La cantidad no puede ser menor a 1", Toast.LENGTH_SHORT).show();
            return;
        }

        double precioUnitario = item.getCantidad() > 0
                ? item.getPrecioTotal() / item.getCantidad()
                : 0;
        item.setCantidad(item.getCantidad() - 1);
        item.setPrecioTotal(precioUnitario * item.getCantidad());

        notifyItemChanged(position);
        notificarTotal();
    }


    private void notificarTotal() {
        if (changeListener == null) return;
        double total = 0;
        for (CarritoItem item : lista) {
            total += item.getPrecioTotal();
        }
        changeListener.onCarritoTotalChanged(total);
    }

    public interface OnCarritoChangeListener {
        void onCarritoTotalChanged(double nuevoTotal);
        void onEliminarItem(int idDetalle, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTipo, tvCantidad, tvPrecio;
        ImageButton btnMas, btnMenos, btnEliminar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre   = itemView.findViewById(R.id.tvNombreCarrito);
            tvTipo     = itemView.findViewById(R.id.tvTipoCarrito);
            tvCantidad = itemView.findViewById(R.id.tvCantidadCarrito);
            tvPrecio   = itemView.findViewById(R.id.tvPrecioItem);
            btnMas     = itemView.findViewById(R.id.btnMas);
            btnMenos   = itemView.findViewById(R.id.btnMenos);
            btnEliminar= itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}
