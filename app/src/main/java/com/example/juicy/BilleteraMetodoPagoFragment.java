package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.juicy.Interface.metodosPagoApi;
import com.example.juicy.Model.EliminarMetodoPagoRequest;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.databinding.FragmentBilleteraMetodoPagoBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BilleteraMetodoPagoFragment extends Fragment {
    private FragmentBilleteraMetodoPagoBinding binding;
    private metodosPagoApi api;
    private MetodosPagoAdapter adapter;

    private String authHeader;
    private int idCliente;
    private static final String BASE_URL = "https://grupotres20252.pythonanywhere.com/";

    private enum SelectedCard {
        PRIMARY,
        SECONDARY
    }


    private SelectedCard selectedCard = SelectedCard.PRIMARY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBilleteraMetodoPagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // === Token e id_cliente desde SharedPreferences (mismo patrón de clase) ===
        SharedPreferences sp = requireActivity().getSharedPreferences("SP_USAT", Context.MODE_PRIVATE);
        String token = sp.getString("tokenJWT", "");
        idCliente = sp.getInt("id_cliente", 0); // guarda esto al iniciar sesión

        if (token == null || token.trim().isEmpty() || idCliente <= 0) {
            Toast.makeText(requireContext(), "Debe autenticarse", Toast.LENGTH_SHORT).show();
            return;
        }
        authHeader = "JWT " + token.trim();

        // === Retrofit ===
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(metodosPagoApi.class);

        // === RecyclerView ===
        adapter = new MetodosPagoAdapter(
                requireContext(),
                new MetodosPagoAdapter.OnItemActionListener() {
                    @Override
                    public void onRequestDelete(@NonNull MetodoPagoEntry m) {
                        // Abrir BottomSheet con preview
                        BorrarMetodoPagoFragment sheet = BorrarMetodoPagoFragment.newInstance(
                                m.getId_metodo_pago(),
                                m.getNum_tarjeta_mask(),
                                m.getTitular(),
                                m.getFecha_expiracion()
                        );
                        sheet.show(getParentFragmentManager(), BorrarMetodoPagoFragment.TAG);
                    }

                    @Override
                    public void onSelect(@NonNull MetodoPagoEntry m) {
                        // Opcional: resaltar seleccionado si lo implementaste en el adapter
                        // adapter.setSelectedId(m.getId_metodo_pago());
                    }
                }
        );
        binding.recyclerMetodos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMetodos.setAdapter(adapter);

        // === Fragment Result: recibe confirmación desde el BottomSheet ===
        getParentFragmentManager().setFragmentResultListener(
                BorrarMetodoPagoFragment.REQ_KEY,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    int id = bundle.getInt(BorrarMetodoPagoFragment.RES_ID, -1);
                    if (id > 0) eliminar(id);
                }
        );

        // === Botón Agregar (navega a tu formulario) ===
        binding.btnAgregar.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.addPaymentMethodFragment)
        );

        // === Carga inicial ===
        listar();
    }

    private void listar() {
        api.listarMetodos(authHeader, idCliente).enqueue(new Callback<RptaGeneral>() {
            @Override public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(), "Código: " + resp.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RptaGeneral r = resp.body();
                if (r.getCode() != 1) {
                    Toast.makeText(requireContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // === parsear Object data con Gson dentro del onResponse (como en clase) ===
                Gson gson = new Gson();
                Type t = new TypeToken<List<MetodoPagoEntry>>(){}.getType();
                List<MetodoPagoEntry> lista = gson.fromJson(gson.toJson(r.getData()), t);
                adapter.setData(lista);
            }
            @Override public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminar(int idMetodo) {
        // usar la clase de request SEPARADA
        EliminarMetodoPagoRequest body = new EliminarMetodoPagoRequest();
        body.setId_metodo_pago(idMetodo);
        body.setId_cliente(idCliente);

        api.eliminarMetodo(authHeader, body).enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(@NonNull Call<RptaGeneral> call, @NonNull Response<RptaGeneral> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(), "Código: " + resp.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RptaGeneral r = resp.body();
                Toast.makeText(requireContext(), r.getMessage(), Toast.LENGTH_SHORT).show();
                if (r.getCode() == 1) listar(); // refrescar
            }

            @Override
            public void onFailure(@NonNull Call<RptaGeneral> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}