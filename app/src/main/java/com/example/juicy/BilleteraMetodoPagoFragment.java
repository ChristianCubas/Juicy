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
import com.example.juicy.Model.ApiMetodosPagoRequest;
import com.example.juicy.Model.ApiMetodosPagoResponse;
import com.example.juicy.Model.EliminarMetodoPagoRequest;
import com.example.juicy.Model.MetodoPagoEntry;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.databinding.FragmentBilleteraMetodoPagoBinding;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.juicy.network.ApiConfig;

public class BilleteraMetodoPagoFragment extends Fragment {

    private FragmentBilleteraMetodoPagoBinding binding;
    private metodosPagoApi api;
    private MetodosPagoAdapter adapter;

    private String authHeader;
    private int idCliente;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBilleteraMetodoPagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // === Token + idCliente desde SharedPreferences ===
        SharedPreferences sp = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = sp.getString("tokenJWT", "");
        this.idCliente = sp.getInt("idCliente", 0);

        if (token == null || token.trim().isEmpty() || this.idCliente <= 0) {
            Toast.makeText(requireContext(), "Debe autenticarse", Toast.LENGTH_SHORT).show();
            return;
        }
        this.authHeader = "JWT " + token.trim();

        // === Retrofit ===
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
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
                        // Si quisieras marcar uno como seleccionado, aquí lo manejarías.
                        // Por ahora solo mostramos la lista.
                    }
                }
        );

        binding.recyclerMetodos.setLayoutManager(
                new LinearLayoutManager(requireContext()));
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

        // === Botón Agregar (navega al formulario para agregar metodo
        binding.btnAgregar.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.addPaymentMethodFragment)
        );

        // === Carga inicial ===
        listar();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al volver desde Agregar / eliminar, refrescamos
        listar();
    }

    private void listar() {
        // Usamos la MISMA API nueva que en MpagoFragment: /api_metodos_pago
        ApiMetodosPagoRequest body = new ApiMetodosPagoRequest();
        body.setId_cliente(this.idCliente);
        body.setGuardar(false);          // solo listar
        body.setNuevo_metodo(null);      // no estamos creando aquí

        api.apiMetodosPago(this.authHeader, body)
                .enqueue(new Callback<ApiMetodosPagoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiMetodosPagoResponse> call,
                                           @NonNull Response<ApiMetodosPagoResponse> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Código: " + resp.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ApiMetodosPagoResponse r = resp.body();
                        if (r.getCode() != 1) {
                            Toast.makeText(requireContext(),
                                    r.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            adapter.setData(Collections.emptyList());
                            return;
                        }

                        ApiMetodosPagoResponse.Data data = r.getData();
                        if (data == null || data.getMetodos() == null) {
                            adapter.setData(Collections.emptyList());
                            return;
                        }

                        List<MetodoPagoEntry> lista = data.getMetodos();
                        adapter.setData(lista);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiMetodosPagoResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error de red",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void eliminar(int idMetodo) {
        EliminarMetodoPagoRequest body = new EliminarMetodoPagoRequest();
        body.setId_metodo_pago(idMetodo);
        body.setId_cliente(this.idCliente);

        api.eliminarMetodo(this.authHeader, body)
                .enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call,
                                           @NonNull Response<RptaGeneral> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Código: " + resp.code(),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RptaGeneral r = resp.body();
                        Toast.makeText(requireContext(),
                                r.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        if (r.getCode() == 1) {
                            listar(); // refrescar lista después de borrar
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error de red",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
