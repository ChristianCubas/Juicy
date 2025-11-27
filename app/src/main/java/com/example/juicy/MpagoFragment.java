package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.metodosPagoApi;
import com.example.juicy.Model.ApiMetodosPagoRequest;
import com.example.juicy.Model.ApiMetodosPagoResponse;
import com.example.juicy.Model.MetodoPagoEntry;
import com.example.juicy.Model.MetodoPagoVentaRequest;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.databinding.FragmentMpagoBinding;
import com.example.juicy.network.ApiConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MpagoFragment extends Fragment {

    private FragmentMpagoBinding binding;

    private metodosPagoApi api;
    private String authHeader;
    private int idCliente;

    // Lista de métodos que vienen de la API
    private final List<MetodoPagoEntry> metodosGuardados = new ArrayList<>();

    // índice del metodo guardado seleccionado
    private int selectedSavedPosition = -1;

    private enum SelectedMethod {
        SAVED,
        OTHER_VISA,
        OTHER_PAYPAL
    }

    private SelectedMethod selectedMethod = SelectedMethod.SAVED;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMpagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) SP: token + idCliente
        SharedPreferences sp = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = sp.getString("tokenJWT", "");
        idCliente    = sp.getInt("idCliente", 0);

        if (token == null || token.trim().isEmpty() || idCliente <= 0) {
            Toast.makeText(requireContext(), "Debe autenticarse", Toast.LENGTH_SHORT).show();
            return;
        }
        authHeader = "JWT " + token.trim();

        // 2) Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(metodosPagoApi.class);

        // 3) Listeners de “Otros métodos”
        setupListeners();

        // 4) Cargar métodos guardados desde la API /api_metodos_pago
        cargarMetodosGuardados();

        // 5) Botón continuar
        binding.continueButton.setOnClickListener(v -> onContinuar());

        // Inicial
        updateSelectionUI();
    }

    // ===================== API: listar métodos guardados =====================

    private void cargarMetodosGuardados() {
        ApiMetodosPagoRequest body = new ApiMetodosPagoRequest();
        body.setId_cliente(idCliente);
        body.setGuardar(false);      // solo listar
        body.setNuevo_metodo(null);  // nada nuevo

        api.apiMetodosPago(authHeader, body).enqueue(new Callback<ApiMetodosPagoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiMetodosPagoResponse> call,
                                   @NonNull Response<ApiMetodosPagoResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(),
                            "Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiMetodosPagoResponse r = response.body();
                if (r.getCode() != 1 || r.getData() == null) {
                    Toast.makeText(requireContext(),
                            r.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<MetodoPagoEntry> lista = r.getData().getMetodos();
                if (lista == null) lista = new ArrayList<>();
                mostrarMetodosGuardados(lista);
            }

            @Override
            public void onFailure(@NonNull Call<ApiMetodosPagoResponse> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarMetodosGuardados(@NonNull List<MetodoPagoEntry> lista) {
        if (!isBindingReady()) {
            return;
        }
        metodosGuardados.clear();
        metodosGuardados.addAll(lista);

        LinearLayout container = binding.savedMethodsContainer;
        container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < metodosGuardados.size(); i++) {
            MetodoPagoEntry m = metodosGuardados.get(i);
            View card = inflater.inflate(
                    R.layout.item_mpago_metodo,
                    container,
                    false
            );

            TextView tvNumero  = card.findViewById(R.id.txtCardNumber);
            TextView tvTitular = card.findViewById(R.id.txtCardHolder);
            TextView tvExp     = card.findViewById(R.id.txtCardExp);
            TextView tvBrand   = card.findViewById(R.id.txtCardBrand);
            ImageView ivStatus = card.findViewById(R.id.imgCardStatus);

            tvNumero.setText(m.getNum_tarjeta_mask());
            tvTitular.setText(m.getTitular());
            tvExp.setText("Exp. " + m.getFecha_expiracion());
            tvBrand.setText("VISA"); // o marca real si la tienes

            final int index = i;
            card.setOnClickListener(v -> {
                selectedMethod = SelectedMethod.SAVED;
                selectedSavedPosition = index;
                updateSavedCardsUI();
                updateSelectionUI();
            });

            container.addView(card);
        }

        if (!metodosGuardados.isEmpty()) {
            selectedMethod = SelectedMethod.SAVED;
            selectedSavedPosition = 0;
            updateSavedCardsUI();
            updateSelectionUI();
        }
    }

    private void updateSavedCardsUI() {
        if (!isBindingReady()) {
            return;
        }
        int checkedIcon = R.drawable.ic_check_circle_file;
        int uncheckedIcon = R.drawable.ic_uncheck_circle;

        LinearLayout container = binding.savedMethodsContainer;
        int count = container.getChildCount();

        for (int i = 0; i < count; i++) {
            View cardView = container.getChildAt(i);
            ImageView status = cardView.findViewById(R.id.imgCardStatus);
            if (status == null) continue;

            status.setImageResource(
                    i == selectedSavedPosition ? checkedIcon : uncheckedIcon
            );
        }
    }

    // ===================== Flujo al pulsar CONTINUAR =====================

    private void onContinuar() {
        switch (selectedMethod) {
            case SAVED:
                pagarConMetodoGuardado();
                break;

            case OTHER_VISA:
                pagarConTarjetaNuevaVisa();
                break;

            case OTHER_PAYPAL:
                Toast.makeText(requireContext(),
                        "Pago con PayPal aún no implementado",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // ---------- 1) Pagar con metodo guardado

    private void pagarConMetodoGuardado() {
        Integer idMetodo = resolveMetodoGuardado();
        if (idMetodo == null || idMetodo <= 0) {
            Toast.makeText(requireContext(), "Seleccione un método válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // lo dejamos final para usarlo dentro del callback
        final int metodoSeleccionado = idMetodo;

        MetodoPagoVentaRequest body = new MetodoPagoVentaRequest();
        body.setId_cliente(idCliente);
        body.setId_metodo_pago(metodoSeleccionado);

        api.setMetodoPagoVenta(authHeader, body).enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(@NonNull Call<RptaGeneral> call,
                                   @NonNull Response<RptaGeneral> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(),
                            "Código: " + resp.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RptaGeneral r = resp.body();
                String mensaje = r.getMessage();
                if (mensaje == null || mensaje.trim().isEmpty()) {
                    mensaje = resp.code() == 200 ? "Operación exitosa" : "Ocurrió un problema";
                }
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();

                boolean operacionExitosa = r.getCode() == 1 || resp.code() == 200;
                if (operacionExitosa) {
                    // 🔹 Guardar el id_metodo_pago en SharedPreferences
                    SharedPreferences sp = requireActivity()
                            .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                    sp.edit()
                            .putInt("idMetodoPagoSeleccionado", metodoSeleccionado)
                            .apply();

                    // Aquí tu equipo ya puede navegar a RESUMEN PEDIDO
                    // NavHostFragment.findNavController(MpagoFragment.this)
                    //        .navigate(R.id.resumenPedidoFragment);
                    irAResumen(getSelectedMetodoText());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RptaGeneral> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------- 2) Pagar con tarjeta nueva (OTHER_VISA) ----------

    private void pagarConTarjetaNuevaVisa() {
        String titular = textOf(binding.otherVisaHolderInput);
        String pan     = textOf(binding.otherVisaNumberInput);
        String exp     = textOf(binding.otherVisaExpInput);
        String cvv     = textOf(binding.otherVisaCvvInput);
        boolean guardar = binding.saveCardSwitch.isChecked();

        // Normalizar PAN
        pan = pan.replaceAll("\\s", "");

        // Validaciones básicas
        if (TextUtils.isEmpty(titular)) {
            toast("Ingrese el titular de la tarjeta");
            return;
        }
        if (TextUtils.isEmpty(pan) || pan.length() < 13) {
            toast("Número de tarjeta inválido");
            return;
        }
        if (TextUtils.isEmpty(exp) || !exp.matches("^(0[1-9]|1[0-2])\\/\\d{2}$")) {
            toast("Fecha de expiración inválida (MM/YY)");
            return;
        }
        if (TextUtils.isEmpty(cvv) || cvv.length() < 3) {
            toast("CVV inválido");
            return;
        }

        // Construimos el objeto de tarjeta
        ApiMetodosPagoRequest.NuevoMetodo nuevo = new ApiMetodosPagoRequest.NuevoMetodo();
        nuevo.setTitular(titular);
        nuevo.setNum_tarjeta(pan);
        nuevo.setFecha_expiracion(exp);
        nuevo.setCvv(cvv);
        nuevo.setCod_paypal(null);

        final String panMask = maskPan(pan);

        if (!guardar) {
            // Pago puntual sin guardar la tarjeta en el listado
            MetodoPagoVentaRequest bodyVenta = new MetodoPagoVentaRequest();
            bodyVenta.setId_cliente(idCliente);
            bodyVenta.setId_metodo_pago(0);
            bodyVenta.setNuevo_metodo(nuevo);

            api.setMetodoPagoVenta(authHeader, bodyVenta).enqueue(new Callback<RptaGeneral>() {
                @Override
                public void onResponse(@NonNull Call<RptaGeneral> call,
                                       @NonNull Response<RptaGeneral> resp) {
                    if (!resp.isSuccessful() || resp.body() == null) {
                        toast("Código: " + resp.code());
                        return;
                    }
                    RptaGeneral rg = resp.body();
                    int idNuevoMetodo = extraerIdMetodo(rg.getData());
                    if (idNuevoMetodo <= 0) {
                        toast("No se pudo asignar el método de pago.");
                        return;
                    }
                    if (idNuevoMetodo > 0) {
                        SharedPreferences sp = requireActivity()
                                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                        sp.edit().putInt("idMetodoPagoSeleccionado", idNuevoMetodo).apply();
                    }
                    String mensaje = rg.getMessage();
                    if (mensaje == null || mensaje.trim().isEmpty()) {
                        mensaje = resp.code() == 200 ? "Operación exitosa" : "Ocurrió un problema";
                    }
                    toast(mensaje);
                    boolean operacionExitosa = rg.getCode() == 1 || resp.code() == 200;
                    if (operacionExitosa) {
                        irAResumen(panMask);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RptaGeneral> call,
                                      @NonNull Throwable t) {
                    toast("Error de red al confirmar pago");
                }
            });
            return;
        }

        // Guardar + listar usando /api_metodos_pago
        ApiMetodosPagoRequest body = new ApiMetodosPagoRequest();
        body.setId_cliente(idCliente);
        body.setGuardar(true);
        body.setNuevo_metodo(nuevo);

        api.apiMetodosPago(authHeader, body).enqueue(new Callback<ApiMetodosPagoResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiMetodosPagoResponse> call,
                                   @NonNull Response<ApiMetodosPagoResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    toast("Código: " + response.code());
                    return;
                }
                ApiMetodosPagoResponse r = response.body();
                if (r.getCode() != 1 || r.getData() == null) {
                    toast(r.getMessage());
                    return;
                }

                List<MetodoPagoEntry> lista = r.getData().getMetodos();
                if (lista == null || lista.isEmpty()) {
                    toast("No se pudo recuperar la tarjeta guardada");
                    return;
                }

                // Asumimos que el último de la lista es el recién insertado
                MetodoPagoEntry ultimo = lista.get(lista.size() - 1);
                final int idNuevoMetodo = ultimo.getId_metodo_pago();

                // Guardamos ese id en SP para RESUMEN PEDIDO
                SharedPreferences sp = requireActivity()
                        .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
                sp.edit()
                        .putInt("idMetodoPagoSeleccionado", idNuevoMetodo)
                        .apply();

                // Ahora sí, usar ese id en api_metodo_pago_venta
                MetodoPagoVentaRequest bodyVenta = new MetodoPagoVentaRequest();
                bodyVenta.setId_cliente(idCliente);
                bodyVenta.setId_metodo_pago(idNuevoMetodo);

                api.setMetodoPagoVenta(authHeader, bodyVenta).enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call,
                                           @NonNull Response<RptaGeneral> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) {
                            toast("Código: " + resp.code());
                            return;
                        }
                        RptaGeneral rg = resp.body();
                        String mensaje = rg.getMessage();
                        if (mensaje == null || mensaje.trim().isEmpty()) {
                            mensaje = resp.code() == 200 ? "Operación exitosa" : "Ocurrió un problema";
                        }
                        toast(mensaje);

                        boolean operacionExitosa = rg.getCode() == 1 || resp.code() == 200;
                        if (operacionExitosa) {
                            // Aquí podrían navegar a RESUMEN PEDIDO
                            irAResumen(ultimo.getNum_tarjeta_mask());

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call,
                                          @NonNull Throwable t) {
                        toast("Error de red al confirmar pago");
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ApiMetodosPagoResponse> call,
                                  @NonNull Throwable t) {
                toast("Error de red al guardar tarjeta");
            }
        });
    }

    @Nullable
    private Integer resolveMetodoGuardado() {
        if (selectedSavedPosition >= 0 &&
                selectedSavedPosition < metodosGuardados.size()) {
            return metodosGuardados.get(selectedSavedPosition).getId_metodo_pago();
        }
        return null;
    }

    @Nullable
    private String getSelectedMetodoText() {
        if (selectedSavedPosition >= 0 &&
                selectedSavedPosition < metodosGuardados.size()) {
            MetodoPagoEntry entry = metodosGuardados.get(selectedSavedPosition);
            return entry.getNum_tarjeta_mask();
        }
        return null;
    }

    private void irAResumen(@Nullable String metodoTexto) {
        if (!isAdded()) {
            return;
        }
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(metodoTexto)) {
            args.putString("metodo_pago", metodoTexto);
        }
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_paymentMethodFragment_to_resumenFragment, args);
    }

    private boolean isBindingReady() {
        return binding != null && isAdded();
    }

    // ===================== Listeners de “Otros métodos” =====================

    private void setupListeners() {
        binding.otherVisaCard.setOnClickListener(v -> {
            selectedMethod = SelectedMethod.OTHER_VISA;
            selectedSavedPosition = -1;
            updateSavedCardsUI();
            updateSelectionUI();
        });

//        binding.otherPaypalCard.setOnClickListener(v -> {
//            selectedMethod = SelectedMethod.OTHER_PAYPAL;
//            selectedSavedPosition = -1;
//            updateSavedCardsUI();
//            updateSelectionUI();
//        });

        if (binding.otherVisaExpInput != null) {
            binding.otherVisaExpInput.addTextChangedListener(expiryWatcher(binding.otherVisaExpInput));
        }
    }

    private void updateSelectionUI() {
        if (binding == null) return;

        int checkedIcon = R.drawable.ic_check_circle_file;
        int uncheckedIcon = R.drawable.ic_uncheck_circle;

        binding.otherVisaStatus.setImageResource(
                selectedMethod == SelectedMethod.OTHER_VISA ? checkedIcon : uncheckedIcon
        );
//        binding.otherPaypalStatus.setImageResource(
//                selectedMethod == SelectedMethod.OTHER_PAYPAL ? checkedIcon : uncheckedIcon
//        );

        binding.otherVisaFields.setVisibility(
                selectedMethod == SelectedMethod.OTHER_VISA ? View.VISIBLE : View.GONE
        );
//        binding.otherPaypalFields.setVisibility(
//                selectedMethod == SelectedMethod.OTHER_PAYPAL ? View.VISIBLE : View.GONE
//        );
    }

    // ===================== Utilitarios =====================

    private String textOf(@NonNull TextView tv) {
        return String.valueOf(tv.getText()).trim();
    }

    private void toast(String m) {
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
    }

    private int extraerIdMetodo(Object data) {
        if (data instanceof Map) {
            Object val = ((Map<?, ?>) data).get("id_metodo_pago");
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
        }
        return -1;
    }

    private String maskPan(String pan) {
        if (pan == null) return "Tarjeta";
        String digits = pan.replaceAll("\\s", "");
        if (digits.length() <= 4) {
            return "**** " + digits;
        }
        String last4 = digits.substring(digits.length() - 4);
        return "**** " + last4;
    }

    private TextWatcher expiryWatcher(@NonNull TextView target) {
        return new TextWatcher() {
            boolean editing;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                if (editing) return;
                editing = true;
                String digits = s.toString().replace("/", "");
                if (digits.length() > 4) digits = digits.substring(0, 4);
                if (digits.length() >= 3) {
                    s.replace(0, s.length(), digits.substring(0, 2) + "/" + digits.substring(2));
                } else {
                    s.replace(0, s.length(), digits);
                }
                editing = false;
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

