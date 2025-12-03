package com.example.juicy.Catalogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.R;

import java.util.List;

public class DireccionesAdapter extends RecyclerView.Adapter<DireccionesAdapter.DireccionViewHolder> {

    public interface OnDireccionListener {
        void onEliminarDireccion(Direccion direccion);
        void onEditarDireccion(Direccion direccion);
    }

    private final List<Direccion> direccionList;
    private final Context context;
    private int selectedPosition = -1;
    private final OnDireccionListener listener;
    private boolean modoGestion = false;

    public DireccionesAdapter(List<Direccion> direccionList,
                              Context context,
                              OnDireccionListener listener) {
        this.direccionList = direccionList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DireccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_direccion, parent, false);
        return new DireccionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DireccionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Direccion direccion = direccionList.get(position);

        holder.txtCategoria.setText(direccion.getCategoria());
        holder.txtDireccion.setText(direccion.getDireccion());
        holder.txtReferencia.setText(direccion.getReferencia());
        holder.txtCiudad.setText(direccion.getCiudad());
        holder.txtCodigoPostal.setText(direccion.getCodigoPostal());

        holder.radioPrincipal.setChecked(position == selectedPosition);
        holder.radioPrincipal.setEnabled(!modoGestion);
        holder.radioPrincipal.setOnClickListener(v -> {
            if (modoGestion) return;
            selectedPosition = position;
            SharedPreferences prefs = context.getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("direccionSeleccionada", direccion.getIdDireccion());
            String direccionCompleta = direccion.getDireccion();
            if (direccion.getReferencia() != null && !direccion.getReferencia().isEmpty()) {
                direccionCompleta += " (" + direccion.getReferencia() + ")";
            }
            editor.putString("direccionTexto", direccionCompleta);
            editor.apply();
            notifyDataSetChanged();
        });

        holder.btnEliminar.setVisibility(modoGestion ? View.VISIBLE : View.GONE);
        holder.btnEliminar.setOnClickListener(v -> {
            if (modoGestion && listener != null) {
                listener.onEliminarDireccion(direccion);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (modoGestion && listener != null) {
                listener.onEditarDireccion(direccion);
            } else if (!modoGestion) {
                holder.radioPrincipal.performClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return direccionList.size();
    }

    public void setModoGestion(boolean gestionar) {
        this.modoGestion = gestionar;
        notifyDataSetChanged();
    }

    public Direccion getDireccionSeleccionada() {
        if (selectedPosition >= 0 && selectedPosition < direccionList.size()) {
            return direccionList.get(selectedPosition);
        }
        return null;
    }

    public void setSelectedDireccionId(int idDireccion) {
        if (idDireccion <= 0) {
            selectedPosition = -1;
            notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < direccionList.size(); i++) {
            if (direccionList.get(i).getIdDireccion() == idDireccion) {
                selectedPosition = i;
                notifyDataSetChanged();
                return;
            }
        }
    }

    static class DireccionViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoria, txtDireccion, txtReferencia, txtCiudad, txtCodigoPostal;
        RadioButton radioPrincipal;
        ImageButton btnEliminar;

        DireccionViewHolder(View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtReferencia = itemView.findViewById(R.id.txtReferencia);
            txtCiudad = itemView.findViewById(R.id.txtCiudad);
            txtCodigoPostal = itemView.findViewById(R.id.txtCodigoPostal);
            radioPrincipal = itemView.findViewById(R.id.radioPrincipal);
            btnEliminar = itemView.findViewById(R.id.btnEliminarDireccion);
        }
    }
}
