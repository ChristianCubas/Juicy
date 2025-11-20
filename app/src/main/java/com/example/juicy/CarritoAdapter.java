package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.juicy.Model.Producto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private List<Producto> listaProductos;

    public CarritoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        holder.txtNombre.setText(producto.getNombre());
        holder.txtDescripcion.setText(producto.getDescripcion());
        holder.txtPrecio.setText("S/ " + producto.getPrecio());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));

        holder.btnSumar.setOnClickListener(v -> {
            int nueva = producto.getCantidad() + 1;
            producto.setCantidad(nueva);
            holder.txtCantidad.setText(String.valueOf(nueva));
            actualizarCantidadEnShared(holder, producto);
        });

        holder.btnRestar.setOnClickListener(v -> {
            int nueva = producto.getCantidad();
            if (nueva > 1) nueva--;

            producto.setCantidad(nueva);
            holder.txtCantidad.setText(String.valueOf(nueva));
            actualizarCantidadEnShared(holder, producto);
        });

        Glide.with(holder.itemView.getContext())
                .load(producto.getImagen_url())
                .into(holder.imgProducto);

        // Carga la imagen solo si existe una URL válida
        if (producto.getImagen_url() != null && !producto.getImagen_url().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(producto.getImagen_url())
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    // ⭐ Actualiza SharedPreferences al cambiar cantidad
    private void actualizarCantidadEnShared(ViewHolder holder, Producto producto) {
        SharedPreferences prefs = holder.itemView.getContext()
                .getSharedPreferences("CARRITO_JUICY", Context.MODE_PRIVATE);
        try {
            JSONArray array = new JSONArray(prefs.getString("carrito", "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getInt("id_producto") == producto.getId_producto()) {
                    obj.put("cantidad", producto.getCantidad());
                    array.put(i, obj);
                    break;
                }
            }
            prefs.edit().putString("carrito", array.toString()).apply();
        } catch (JSONException e) { }
    }


    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescripcion, txtPrecio, txtCantidad;
        ImageView imgProducto;
        ImageButton btnSumar,btnRestar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
        }
    }
}
