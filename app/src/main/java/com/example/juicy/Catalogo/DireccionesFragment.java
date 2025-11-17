package com.example.juicy.Catalogo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.juicy.Catalogo.ResponseDirecciones;
import com.example.juicy.Catalogo.Direccion;
import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.R;
import com.example.juicy.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DireccionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DireccionesAdapter direccionesAdapter;
    private List<Direccion> direccionList = new ArrayList<>();

    public DireccionesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_direcciones, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewDirecciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        direccionesAdapter = new DireccionesAdapter(direccionList, getContext());
        recyclerView.setAdapter(direccionesAdapter);

        // Obtener el idUsuario desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("idCliente", 0); // Aquí se obtiene el idCliente guardado en SharedPreferences
        Log.d("MpagoFragment", "idCliente obtenido: " + idUsuario);
        // Verificar si se obtuvo un idUsuario válido
        if (idUsuario != 0) {
            obtenerDirecciones(idUsuario);  // Llamar a la función con el idUsuario
        } else {
            // Manejo de error si no se pudo obtener el idUsuario
            Toast.makeText(getContext(), "No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show();
        }

        Button btnAgregar = rootView.findViewById(R.id.btnAgregarDireccion);
        btnAgregar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.agregarDirecciones));

        Button btnContinuar = rootView.findViewById(R.id.btnContinuar);
        btnContinuar.setOnClickListener(v -> {
            Direccion seleccionada = direccionesAdapter.getDireccionSeleccionada();
            if (seleccionada == null) {
                Toast.makeText(getContext(), "Por favor, selecciona una dirección.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefsLocal = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefsLocal.edit();
            editor.putInt("direccionSeleccionada", seleccionada.getIdDireccion());

            String direccionTexto = seleccionada.getDireccion();
            if (seleccionada.getReferencia() != null && !seleccionada.getReferencia().isEmpty()) {
                direccionTexto += " (" + seleccionada.getReferencia() + ")";
            }
            editor.putString("direccionTexto", direccionTexto);
            editor.apply();

            Navigation.findNavController(v).navigate(R.id.action_direcciones_to_metodoPago);
        });

        return rootView;
    }

    private void obtenerDirecciones(int idUsuario) {
        // Crear el servicio de API de Retrofit
        DambJuiceApi apiService = RetrofitClient.getApiService();

        // Crear el cuerpo de la solicitud con el idUsuario
        Map<String, Integer> body = new HashMap<>();
        body.put("id_cliente", idUsuario);  // Enviar el id_cliente en el cuerpo

        // Obtener el token JWT desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);  // Obtener el token JWT

        // Verificar que el token y el id_cliente no sean nulos o 0
        if (token == null || idUsuario == 0) {
            Toast.makeText(getContext(), "No se ha encontrado el token de autenticación o el id_cliente.", Toast.LENGTH_SHORT).show();
            return;  // Si el token o el id_cliente son inválidos, no hacer la solicitud
        }

        // Agregar el prefijo "JWT " al token antes de enviarlo en los encabezados
        String authHeader = "JWT " + token;

        // Realizar la solicitud POST con el token en los encabezados y el cuerpo
        Call<ResponseDirecciones> call = apiService.listarDirecciones(authHeader, body);

        call.enqueue(new Callback<ResponseDirecciones>() {
            @Override
            public void onResponse(Call<ResponseDirecciones> call, Response<ResponseDirecciones> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obtener la lista de direcciones desde la respuesta
                    List<Direccion> direcciones = response.body().getDirecciones();

                    direccionList.clear();
                    direccionList.addAll(direcciones);

                    // Notificar al adaptador que los datos han cambiado
                    direccionesAdapter.notifyDataSetChanged();

                    SharedPreferences prefsLocal = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                    int idSeleccionado = prefsLocal.getInt("direccionSeleccionada", -1);
                    direccionesAdapter.setSelectedDireccionId(idSeleccionado);
                } else {
                    Log.e("DireccionesFragment", "Error en la respuesta: " + response.message());
                    Toast.makeText(getContext(), "Error al obtener direcciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDirecciones> call, Throwable t) {
                Log.e("DireccionesFragment", "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
