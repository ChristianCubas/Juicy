package com.example.juicy;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.network.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarDirecciones extends Fragment implements OnMapReadyCallback {

    private EditText etCategoria, etReferencia, etCiudad, etBuscarDireccion;
    private Switch swEsPrincipal;
    private TextView tvCoordenadas;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marcador;
    private LatLng ubicacionSeleccionada;
    private Address ultimaDireccion;
    private String placeId;

    private Button btnGuardar;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;
    private boolean actualizandoCampoDireccion = false;

    private static final LatLng DEFAULT_LOCATION = new LatLng(-6.7714, -79.8409); // Chiclayo

    private Integer idDireccionEdit = null;
    private final boolean isEditMode = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_direcciones, container, false);

        etCategoria = view.findViewById(R.id.etCategoria);
        etReferencia = view.findViewById(R.id.etReferencia);
        etCiudad = view.findViewById(R.id.etCiudad);
        etBuscarDireccion = view.findViewById(R.id.etBuscarDireccion);
        swEsPrincipal = view.findViewById(R.id.swEsPrincipal);
        tvCoordenadas = view.findViewById(R.id.tvCoordenadas);
        btnGuardar = view.findViewById(R.id.btnGuardarDireccion);
        btnGuardar.setEnabled(true);

        mapView = view.findViewById(R.id.mapDireccion);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, null);
            mapView.getMapAsync(this);
        }

        btnGuardar.setOnClickListener(v -> guardarDireccion());

        etBuscarDireccion.addTextChangedListener(new SimpleWatcher(text -> {
            if (actualizandoCampoDireccion) {
                return;
            }
            programarBusqueda(text);
            actualizarEstadoGuardar();
        }));
        etCategoria.addTextChangedListener(new SimpleWatcher(t -> actualizarEstadoGuardar()));
        etReferencia.addTextChangedListener(new SimpleWatcher(t -> actualizarEstadoGuardar()));
        etCiudad.addTextChangedListener(new SimpleWatcher(t -> actualizarEstadoGuardar()));

        if (getArguments() != null && getArguments().containsKey("id_direccion")) {
            idDireccionEdit = getArguments().getInt("id_direccion");
            etCategoria.setText(getArguments().getString("categoria", ""));
            etBuscarDireccion.setText(getArguments().getString("direccion", ""));
            etReferencia.setText(getArguments().getString("referencia", ""));
            etCiudad.setText(getArguments().getString("ciudad", ""));
            swEsPrincipal.setChecked(getArguments().getBoolean("es_principal", false));
            btnGuardar.setText("Actualizar direccion");
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 14f));
        googleMap.setOnMapClickListener(this::resolverDireccionDesdeMapa);
        googleMap.setOnMarkerClickListener(marker -> {
            resolverDireccionDesdeMapa(marker.getPosition());
            return true;
        });
    }

    private void programarBusqueda(CharSequence texto) {
        if (pendingSearch != null) {
            handler.removeCallbacks(pendingSearch);
        }
        if (TextUtils.isEmpty(texto) || texto.length() < 4) {
            return;
        }
        final String query = texto.toString();
        pendingSearch = () -> buscarDireccion(query);
        handler.postDelayed(pendingSearch, 900);
    }

    private void buscarDireccion(String query) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocationName(query, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address address = direcciones.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                requireActivity().runOnUiThread(() ->
                        marcarUbicacion(latLng, address, false, true));
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                "Ubicacion encontrada, toca el mapa o el pin para confirmar",
                                Toast.LENGTH_SHORT).show());
            }
        } catch (IOException ignored) {
        }
    }

    private void resolverDireccionDesdeMapa(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = (direcciones != null && !direcciones.isEmpty()) ? direcciones.get(0) : null;
            marcarUbicacion(latLng, address, true, true);
        } catch (IOException ignored) {
            marcarUbicacion(latLng, null, true, false);
        }
    }

    private void marcarUbicacion(@NonNull LatLng latLng, @Nullable Address address, boolean confirmarSeleccion, boolean actualizarBusqueda) {
        if (confirmarSeleccion) {
            ubicacionSeleccionada = latLng;
        }
        if (marcador != null) {
            marcador.remove();
        }
        if (googleMap != null) {
            marcador = googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicacion seleccionada"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        tvCoordenadas.setText(String.format(Locale.getDefault(),
                "Lat: %.5f, Lng: %.5f", latLng.latitude, latLng.longitude));

        if (address == null) {
            address = ultimaDireccion;
        } else {
            ultimaDireccion = address;
            placeId = !TextUtils.isEmpty(address.getFeatureName())
                    ? address.getFeatureName()
                    : address.getAddressLine(0);
        }

        if (address != null) {
            if (TextUtils.isEmpty(etCiudad.getText()) && address.getLocality() != null) {
                etCiudad.setText(address.getLocality());
            }
            if (actualizarBusqueda) {
                actualizandoCampoDireccion = true;
                etBuscarDireccion.setText(address.getAddressLine(0));
                etBuscarDireccion.setSelection(etBuscarDireccion.getText().length());
                actualizandoCampoDireccion = false;
            }
        }
        actualizarEstadoGuardar();
    }

    private void actualizarEstadoGuardar() {
        boolean camposOk = !TextUtils.isEmpty(etBuscarDireccion.getText())
                && !TextUtils.isEmpty(etReferencia.getText())
                && !TextUtils.isEmpty(etCiudad.getText())
                && !TextUtils.isEmpty(etCategoria.getText());
        btnGuardar.setEnabled(camposOk);
    }

    private void guardarDireccion() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        int idCliente = prefs.getInt("idCliente", 0);

        if (token == null || idCliente == 0) {
            Toast.makeText(requireContext(),
                    "Sesion no valida. Inicie sesion nuevamente.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (idDireccionEdit == null) {
            Toast.makeText(requireContext(), "Direccion no valida", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoria = etCategoria.getText().toString().trim();
        String direccionTexto = etBuscarDireccion.getText().toString().trim();
        String referencia = etReferencia.getText().toString().trim();
        String ciudad = etCiudad.getText().toString().trim();

        boolean hasError = false;
        if (TextUtils.isEmpty(categoria)) { etCategoria.setError("Requerido"); hasError = true; }
        if (TextUtils.isEmpty(direccionTexto) || direccionTexto.length() < 4) {
            etBuscarDireccion.setError("Ingresa la calle o direccion exacta"); hasError = true;
        }
        if (TextUtils.isEmpty(referencia)) { etReferencia.setError("Requerido"); hasError = true; }
        if (TextUtils.isEmpty(ciudad)) { etCiudad.setError("Requerido"); hasError = true; }
        if (hasError) {
            Toast.makeText(requireContext(),
                    "Completa los campos obligatorios.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("id_usuario", idCliente);
        body.put("categoria", categoria);
        body.put("direccion", direccionTexto);
        body.put("referencia", referencia);
        body.put("ciudad", ciudad);
        body.put("codigo_postal", ultimaDireccion != null ? safeValue(ultimaDireccion.getPostalCode()) : "");
        body.put("es_principal", swEsPrincipal.isChecked() ? 1 : 0);
        if (ubicacionSeleccionada != null) {
            body.put("latitud", ubicacionSeleccionada.latitude);
            body.put("longitud", ubicacionSeleccionada.longitude);
            body.put("place_id", placeId);
        }

        DambJuiceApi api = RetrofitClient.getApiService();
        api.actualizarDireccion("JWT " + token.trim(), idDireccionEdit, body)
                .enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call,
                                           @NonNull Response<RptaGeneral> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "No se pudo procesar la direccion (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RptaGeneral rpta = response.body();
                        Toast.makeText(requireContext(),
                                rpta.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        if (rpta.getCode() == 1) {
                            NavController navController = NavHostFragment.findNavController(EditarDirecciones.this);
                            navController.popBackStack();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error al guardar: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String safeValue(String value) {
        return value == null ? "" : value;
    }

    private static class SimpleWatcher implements TextWatcher {
        interface OnTextChange {
            void onChange(CharSequence text);
        }

        private final OnTextChange callback;

        SimpleWatcher(OnTextChange callback) {
            this.callback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            callback.onChange(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    public void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapView != null) mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (mapView != null) mapView.onDestroy();
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}
