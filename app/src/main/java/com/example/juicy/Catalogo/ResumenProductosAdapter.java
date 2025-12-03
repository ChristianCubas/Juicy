package com.example.juicy.Catalogo;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Model.CarritoResponse;
import com.example.juicy.Model.ProcesadorTexto;
import com.example.juicy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResumenProductosAdapter extends RecyclerView.Adapter<ResumenProductosAdapter.ViewHolder> {

    private final List<CarritoResponse.Producto> productos = new ArrayList<>();

    public void setData(List<CarritoResponse.Producto> nuevos) {
        productos.clear();
        if (nuevos != null) {
            productos.addAll(nuevos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resumen_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarritoResponse.Producto p = productos.get(position);
        holder.tvNombre.setText(p.getNombreProducto());

        String personalizacion = ProcesadorTexto.formatearPersonalizacion(p.getPersonalizaciones());
        if (!TextUtils.isEmpty(personalizacion)) {
            holder.tvPersonalizacion.setVisibility(View.VISIBLE);
            holder.tvPersonalizacion.setText(personalizacion);
        } else {
            holder.tvPersonalizacion.setVisibility(View.GONE);
        }

        holder.tvCantidad.setText(String.format(Locale.getDefault(), "x%d", p.getCantidad()));
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "S/. %.2f", p.getPrecioTotal()));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPersonalizacion, tvCantidad, tvPrecio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreResumen);
            tvPersonalizacion = itemView.findViewById(R.id.tvPersonalizacionResumen);
            tvCantidad = itemView.findViewById(R.id.tvCantidadResumen);
            tvPrecio = itemView.findViewById(R.id.tvPrecioResumen);
        }
    }
}
