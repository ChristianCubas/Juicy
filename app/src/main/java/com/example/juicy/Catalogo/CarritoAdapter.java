package com.example.juicy.Catalogo;

import android.content.Context;
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

    private final Context context;
    private final List<CarritoItem> lista;

    public CarritoAdapter(Context context, List<CarritoItem> lista) {
        this.context = context;
        this.lista = lista;
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

        holder.tvNombre.setText(item.getNombreProducto());
        holder.tvTipo.setText(item.getTipo());
        holder.tvCantidad.setText(String.format("%02d", item.getCantidad()));
        holder.tvPrecio.setText(String.format("S/ %.2f", item.getPrecioTotal()));

        // Por ahora solo mostramos Toast, luego conectamos con las APIs de actualizar / eliminar
        holder.btnMas.setOnClickListener(v ->
                Toast.makeText(context, "Aquí iría + cantidad", Toast.LENGTH_SHORT).show());

        holder.btnMenos.setOnClickListener(v ->
                Toast.makeText(context, "Aquí iría - cantidad", Toast.LENGTH_SHORT).show());

        holder.btnEliminar.setOnClickListener(v ->
                Toast.makeText(context, "Aquí iría eliminar producto", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return lista.size();
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
