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

public class DireccionesAdapter extends RecyclerView.Adapter<DireccionesAdapter.DireccionViewHolder> {

    private List<Direccion> direccionList;
    private Context context;
    private int selectedPosition = -1;  // Variable para almacenar la posición del RadioButton seleccionado

    public DireccionesAdapter(List<Direccion> direccionList, Context context) {
        this.direccionList = direccionList;
        this.context = context;
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

        // Asignar los valores de la dirección
        holder.txtCategoria.setText(direccion.getCategoria());
        holder.txtDireccion.setText(direccion.getDireccion());
        holder.txtReferencia.setText(direccion.getReferencia());
        holder.txtCiudad.setText(direccion.getCiudad());
        holder.txtCodigoPostal.setText(direccion.getCodigoPostal());

        // Establecer si el RadioButton está marcado o no
        holder.radioPrincipal.setChecked(position == selectedPosition);

        // Listener para manejar el estado de selección
        // En tu adaptador, cuando un RadioButton es seleccionado
        holder.radioPrincipal.setOnClickListener(v -> {
            selectedPosition = position;
            // Guardar la dirección seleccionada en SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("direccionSeleccionada", direccion.getIdDireccion());  // Guarda el ID de la dirección
            editor.apply();  // Asegúrate de guardar
            notifyDataSetChanged();  // Actualiza el RecyclerView
        });

    }

    @Override
    public int getItemCount() {
        return direccionList.size();
    }

    public static class DireccionViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoria, txtDireccion, txtReferencia, txtCiudad, txtCodigoPostal;
        RadioButton radioPrincipal;  // Cambié CheckBox por RadioButton

        public DireccionViewHolder(View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtReferencia = itemView.findViewById(R.id.txtReferencia);
            txtCiudad = itemView.findViewById(R.id.txtCiudad);
            txtCodigoPostal = itemView.findViewById(R.id.txtCodigoPostal);
            radioPrincipal = itemView.findViewById(R.id.radioPrincipal);  // Cambié CheckBox por RadioButton
        }
    }
}
