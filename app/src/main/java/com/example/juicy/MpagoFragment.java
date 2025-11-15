package com.example.juicy;

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

import com.example.juicy.Catalogo.MetodoPago;
import com.example.juicy.Catalogo.MetodosPagoAdapter;
import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MpagoFragment extends Fragment {

    private RecyclerView recyclerView;
    private MetodosPagoAdapter metodosPagoAdapter;
    private List<MetodoPago> metodoPagoList = new ArrayList<>();

    public MpagoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mpago, container, false);

        recyclerView = rootView.findViewById(R.id.paymentMethodsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        metodosPagoAdapter = new MetodosPagoAdapter(metodoPagoList, getContext());
        recyclerView.setAdapter(metodosPagoAdapter);

        // Obtener el idCliente desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", 0);
        Log.d("MpagoFragment", "idCliente obtenido: " + idCliente);  // Verifica el valor en el Logcat

        if (idCliente != 0) {
            obtenerMetodosPago(idCliente);  // Llamar a la función con el idCliente
        } else {
            Toast.makeText(getContext(), "No se pudo obtener el ID del cliente.", Toast.LENGTH_SHORT).show();
        }


        // Botón para continuar
        Button btnContinuar = rootView.findViewById(R.id.continueButton);
        btnContinuar.setOnClickListener(v -> {
            // Verificar que se haya seleccionado un método de pago
            SharedPreferences sp = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
            int metodoPagoSeleccionado = sp.getInt("metodoPagoSeleccionado", -1);  // -1 es el valor por defecto

            if (metodoPagoSeleccionado != -1) {
                // Si un metodo de pago ha sido seleccionado, navegar al siguiente fragmento
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_direcciones_to_metodoPago); // Asegúrate de usar el ID correcto
            } else {
                // Si no se seleccionó un metodo de pago, mostrar un mensaje de error
                Toast.makeText(getContext(), "Por favor, selecciona un método de pago.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
    private void obtenerMetodosPago(int idCliente) {
        // Crear el servicio de API de Retrofit
        DambJuiceApi apiService = RetrofitClient.getApiService();

        // Crear el cuerpo de la solicitud con el idCliente
        Map<String, Integer> body = new HashMap<>();
        body.put("id_cliente", idCliente);  // Enviar el id_cliente en el cuerpo

        // Obtener el token JWT desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);  // Obtener el token JWT

        // Verificar que el token y el id_cliente no sean nulos o 0
        if (token == null || idCliente == 0) {
            Toast.makeText(getContext(), "No se ha encontrado el token de autenticación o el id_cliente.", Toast.LENGTH_SHORT).show();
            return;  // Si el token o el id_cliente son inválidos, no hacer la solicitud
        }

        // Agregar el prefijo "JWT " al token antes de enviarlo en los encabezados
        String authHeader = "JWT " + token;

        // Realizar la solicitud POST con el token en los encabezados y el cuerpo
        Call<RptaGeneral> call = apiService.listarMetodosPago(authHeader, body);

        call.enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RptaGeneral rpta = response.body();

                    if (rpta.getCode() == 1 && rpta.getData() instanceof List<?>) {
                        // Convertir el 'data' en una lista de MetodoPago
                        List<MetodoPago> metodosPago = (List<MetodoPago>) rpta.getData();

                        // Limpiar la lista de métodos de pago y agregar los nuevos
                        metodoPagoList.clear();
                        metodoPagoList.addAll(metodosPago);

                        // Notificar al adaptador que los datos han cambiado
                        metodosPagoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No se pudieron cargar los métodos de pago.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MpagoFragment", "Error en la respuesta: " + response.message());
                    Toast.makeText(getContext(), "Error al obtener los métodos de pago", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Log.e("MpagoFragment", "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
