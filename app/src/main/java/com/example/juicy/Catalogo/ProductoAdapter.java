package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Interface.OnProductoClickListener;
import com.example.juicy.R;
import com.example.juicy.Model.Producto;
import com.example.juicy.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final Context context;
    private final List<Producto> lista;
    private final OnProductoClickListener listener;

    public ProductoAdapter(Context context, List<Producto> lista, OnProductoClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoClick(p); // Llama a la acción principal
            }
        });
        holder.btnAgregar.setOnClickListener(v -> agregarAlCarrito(p.getIdProducto()));
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnAgregar;
        TextView tvNombre, tvPrecio;
        NetworkImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }


    // ================================
    //   Metodo agregar carrito
    // ===============================
    private void agregarAlCarrito(int idProducto) {

        String url = "https://grupotres20252.pythonanywhere.com/api_agregar_al_carrito";

        // Obtener token
        SharedPreferences prefs = context.getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        int idCliente = prefs.getInt("idCliente", 0);

        if (token == null || idCliente == 0) {
            Toast.makeText(context, "Debe iniciar sesion para agregar productos.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", idCliente);
            body.put("id_producto", idProducto);
            body.put("cantidad", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    Toast.makeText(context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(context, "Error al agregar: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}



