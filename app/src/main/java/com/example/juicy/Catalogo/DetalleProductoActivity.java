package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.juicy.Model.Producto;
import com.example.juicy.R;
import com.example.juicy.network.VolleySingleton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DetalleProductoActivity extends AppCompatActivity {

    private static final String TAG = "DetalleProducto";

    // --- Vistas de la UI ---
    private ImageButton btnAtras;
    private NetworkImageView imgProductoDetalle;
    private TextView tvNombreDetalle, tvDescripcionDetalle, tvPrecioDetalle, tvCantidad, tvTotalFinal;
    private LinearLayout triggerPersonalizacion, seccionPersonalizacion;
    private ImageView ivTogglePersonalizacion;
    private ImageButton btnRestarCantidad, btnSumarCantidad;
    private MaterialButtonToggleGroup toggleGroupTamaño, toggleGroupAzucar;
    private Button btnAnadirCarrito;

    // --- Datos y Lógica ---
    private Producto productoActual;
    private int idProducto;
    private String tokenJWT;
    private int cantidadActual = 1;
    private double precioCalculado = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        initVistas();

        idProducto = getIntent().getIntExtra("ID_PRODUCTO", 0);

        SharedPreferences prefs = getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        tokenJWT = prefs.getString("tokenJWT", null);

        if (idProducto == 0 || tokenJWT == null) {
            Toast.makeText(this, "Error: No se pudo cargar el producto.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error: idProducto (" + idProducto + ") o tokenJWT no disponible.");
            finish();
            return;
        }

        btnAtras.setOnClickListener(v -> finish());
        cargarDetallesProducto();
    }

    private void initVistas() {
        btnAtras = findViewById(R.id.btnAtras);
        imgProductoDetalle = findViewById(R.id.imgProductoDetalle);
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle);
        tvDescripcionDetalle = findViewById(R.id.tvDescripcionDetalle);
        tvPrecioDetalle = findViewById(R.id.tvPrecioDetalle);
        tvTotalFinal = findViewById(R.id.tvTotalFinal);
        tvCantidad = findViewById(R.id.tvCantidad);

        btnRestarCantidad = findViewById(R.id.btnRestarCantidad);
        btnSumarCantidad = findViewById(R.id.btnSumarCantidad);

        triggerPersonalizacion = findViewById(R.id.triggerPersonalizacion);
        seccionPersonalizacion = findViewById(R.id.seccionPersonalizacion);
        ivTogglePersonalizacion = findViewById(R.id.ivTogglePersonalizacion);
        toggleGroupTamaño = findViewById(R.id.toggleGroupTamaño);
        toggleGroupAzucar = findViewById(R.id.toggleGroupAzucar);

        btnAnadirCarrito = findViewById(R.id.btnAnadirCarrito);
    }

    private void cargarDetallesProducto() {
        String URL_API = com.example.juicy.network.ApiConfig.BASE_URL + "api_producto/" + idProducto;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, URL_API, null,
                response -> {
                    try {
                        if (response.getInt("code") != 1) {
                            Toast.makeText(this, "Error del servidor al cargar producto", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject data = response.getJSONObject("data");
                        productoActual = new Producto();
                        productoActual.setId_producto(data.getInt("id_producto"));
                        productoActual.setNombre(data.getString("nombre"));
                        productoActual.setDescripcion(data.getString("descripcion"));
                        productoActual.setPrecio(data.getDouble("precio_base"));
                        productoActual.setImagen_url(data.optString("imagen_url"));

                        productoActual.setPermite_personalizacion(data.getInt("permite_personalizacion"));
                        productoActual.setPrecio_extra_small(data.getDouble("precio_extra_small"));
                        productoActual.setPrecio_extra_regular(data.getDouble("precio_extra_regular"));
                        productoActual.setPrecio_extra_alto(data.getDouble("precio_extra_alto"));
                        productoActual.setNivel_azucar_defecto(data.getString("nivel_azucar_defecto"));

                        configurarUI();

                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear JSON: " + e.getMessage());
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + tokenJWT);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }

    private void configurarUI() {
        if (productoActual == null) return;

        tvNombreDetalle.setText(productoActual.getNombre());
        tvDescripcionDetalle.setText(productoActual.getDescripcion());
        tvCantidad.setText(String.valueOf(cantidadActual));
        tvPrecioDetalle.setText("S/ " + String.format("%.2f", productoActual.getPrecio()));

        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();
        imgProductoDetalle.setImageUrl(productoActual.getImagen_url(), imageLoader);

        if (productoActual.getPermite_personalizacion() == 1) {
            triggerPersonalizacion.setVisibility(View.VISIBLE);
            triggerPersonalizacion.setOnClickListener(v -> {
                if (seccionPersonalizacion.getVisibility() == View.VISIBLE) {
                    seccionPersonalizacion.setVisibility(View.GONE);
                    ivTogglePersonalizacion.setRotation(0);
                } else {
                    seccionPersonalizacion.setVisibility(View.VISIBLE);
                    ivTogglePersonalizacion.setRotation(180);
                }
            });

            toggleGroupTamaño.check(R.id.btnTamañoRegular);
            String azucarDefecto = productoActual.getNivel_azucar_defecto();
            if (azucarDefecto.equalsIgnoreCase("BAJO")) toggleGroupAzucar.check(R.id.btnAzucarBajo);
            else if (azucarDefecto.equalsIgnoreCase("CERO")) toggleGroupAzucar.check(R.id.btnAzucarCero);
            else toggleGroupAzucar.check(R.id.btnAzucarNormal);

        } else {
            triggerPersonalizacion.setVisibility(View.GONE);
            seccionPersonalizacion.setVisibility(View.GONE);
        }

        configurarListenersDeCalculo();
        actualizarPrecio();
    }

    private void configurarListenersDeCalculo() {
        btnRestarCantidad.setOnClickListener(v -> {
            if (cantidadActual > 1) {
                cantidadActual--;
                tvCantidad.setText(String.valueOf(cantidadActual));
                actualizarPrecio();
            }
        });

        btnSumarCantidad.setOnClickListener(v -> {
            cantidadActual++;
            tvCantidad.setText(String.valueOf(cantidadActual));
            actualizarPrecio();
        });

        toggleGroupTamaño.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) actualizarPrecio();
        });

        btnAnadirCarrito.setOnClickListener(v -> enviarCarritoALaAPI());
    }

    private void actualizarPrecio() {
        if (productoActual == null) return;

        double precioBase = productoActual.getPrecio();
        double precioExtra = 0.0;

        int idTamañoSeleccionado = toggleGroupTamaño.getCheckedButtonId();
        if (idTamañoSeleccionado == R.id.btnTamañoSmall) precioExtra = productoActual.getPrecio_extra_small();
        else if (idTamañoSeleccionado == R.id.btnTamañoRegular) precioExtra = productoActual.getPrecio_extra_regular();
        else if (idTamañoSeleccionado == R.id.btnTamañoAlto) precioExtra = productoActual.getPrecio_extra_alto();

        double precioUnitario = precioBase + precioExtra;
        precioCalculado = precioUnitario * cantidadActual;

        tvTotalFinal.setText("S/ " + String.format("%.2f", precioCalculado));
    }

    private void enviarCarritoALaAPI() {
        if (productoActual == null) return;

        SharedPreferences prefs = getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("idCliente", 0);

        if (idCliente == 0) {
            Toast.makeText(this, "Sesión no válida, inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String tamañoSeleccionado = "Regular"; // Default
        int idTamaño = toggleGroupTamaño.getCheckedButtonId();
        if (idTamaño == R.id.btnTamañoSmall) tamañoSeleccionado = "Small";
        else if (idTamaño == R.id.btnTamañoAlto) tamañoSeleccionado = "Alto";

        String azucarSeleccionado = "Normal"; // Default
        int idAzucar = toggleGroupAzucar.getCheckedButtonId();
        if (idAzucar == R.id.btnAzucarCero) azucarSeleccionado = "Cero";
        else if (idAzucar == R.id.btnAzucarBajo) azucarSeleccionado = "Bajo";

        JSONObject personalizaciones = new JSONObject();
        try {
            if (productoActual.getPermite_personalizacion() == 1) {
                personalizaciones.put("Tamaño", tamañoSeleccionado);
                personalizaciones.put("Azúcar", azucarSeleccionado);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        double precioUnitarioFinal = precioCalculado / cantidadActual;

        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", idCliente);
            body.put("id_producto", productoActual.getId_producto());
            body.put("cantidad", cantidadActual);
            body.put("precio_unitario_final", precioUnitarioFinal);
            body.put("personalizaciones", personalizaciones.toString());
        } catch (JSONException e) { e.printStackTrace(); }

        String URL_ADD = com.example.juicy.network.ApiConfig.BASE_URL + "api_agregar_carritoFCN";
        Log.d(TAG, "Enviando carrito a: " + URL_ADD);
        Log.d(TAG, "Datos: " + body.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_ADD,
                body,
                response -> {
                    try {
                        if (response.getInt("code") == 1) {
                            Toast.makeText(this, "¡Producto agregado! 🛒", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = response.optString("message", "Error desconocido");
                            Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error de respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String mensajeError = "Error de conexión desconocido";

                    if (error instanceof TimeoutError) {
                        mensajeError = "Tiempo de espera agotado. Servidor lento.";
                    } else if (error instanceof NoConnectionError) {
                        mensajeError = "Sin conexión a Internet.";
                    } else if (error instanceof AuthFailureError) {
                        mensajeError = "Error de autenticación (Token).";
                    } else if (error instanceof ServerError) {
                        mensajeError = "Error interno del servidor (500).";
                    } else if (error instanceof NetworkError) {
                        mensajeError = "Error de red.";
                    } else if (error instanceof ParseError) {
                        mensajeError = "Error al leer respuesta del servidor.";
                    }

                    // Intentar obtener el error real del cuerpo de la respuesta
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String bodyError = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e(TAG, "Error Volley Body: " + bodyError);
                            mensajeError += " Detalle: " + bodyError;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Log.e(TAG, "Error Volley: " + error.toString());
                    Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (tokenJWT != null) {
                    headers.put("Authorization", "JWT " + tokenJWT);
                }
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).getRequestQueue().add(request);
    }
}
