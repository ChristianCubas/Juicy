package com.example.juicy.Catalogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.R;
import com.example.juicy.Model.Producto;
import com.example.juicy.network.VolleySingleton;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final Context context;
    private final List<Producto> lista;

    public ProductoAdapter(Context context, List<Producto> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = lista.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvPrecio.setText("S/ " + p.getPrecio());
        holder.imgProducto.setImageUrl(
                p.getImagen_url(),
                VolleySingleton.getInstance(context).getImageLoader()
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio;
        NetworkImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}
