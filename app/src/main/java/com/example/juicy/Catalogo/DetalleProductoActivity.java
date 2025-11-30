package com.example.juicy.Catalogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.juicy.network.ApiConfig;
import com.example.juicy.network.VolleySingleton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;
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

    private LinearLayout triggerPersonalizacion, seccionPersonalizacion, contenedorOpciones;
    private ImageView ivTogglePersonalizacion;

    private ImageButton btnRestarCantidad, btnSumarCantidad;
    private Button btnAnadirCarrito;

    // --- Datos y Lógica ---
    private Producto productoActual;
    private int idProducto;
    private String tokenJWT;
    private int cantidadActual = 1;
    private double precioBase = 0.0;
    private double precioExtras = 0.0;
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

        contenedorOpciones = findViewById(R.id.contenedorOpcionesDinamicas);

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

                        productoActual.setStock(data.optInt("stock", 0));
                        productoActual.setPermite_personalizacion(data.optInt("permite_personalizacion"));

                        // Guardamos el JSON de configuración como String
                        String configJson = data.optString("config_personalizacion", null);
                        productoActual.setConfig_personalizacion(configJson);

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

        precioBase = productoActual.getPrecio();
        tvPrecioDetalle.setText("Precio base: S/ " + String.format("%.2f", precioBase));

        if (productoActual.getImagen_url() != null && !productoActual.getImagen_url().isEmpty()) {
            String fullUrl = productoActual.getImagen_url();
            // Corrección de URL relativa usando ApiConfig.BASE_URL
            if (!fullUrl.startsWith("http")) {
                fullUrl = ApiConfig.BASE_URL + fullUrl.replaceFirst("^/+", "");
            }
            imgProductoDetalle.setImageUrl(fullUrl, VolleySingleton.getInstance(this).getImageLoader());
        }

        // Lógica de Personalización Dinámica
        if (productoActual.getPermite_personalizacion() == 1) {
            triggerPersonalizacion.setVisibility(View.VISIBLE);

            // Dibujar las opciones basadas en el JSON
            String jsonConfig = productoActual.getConfig_personalizacion();
            if (jsonConfig != null && !jsonConfig.equals("null")) {
                dibujarOpcionesDinamicas(jsonConfig);
            }
            triggerPersonalizacion.setOnClickListener(v -> {
                if (seccionPersonalizacion.getVisibility() == View.VISIBLE) {
                    seccionPersonalizacion.setVisibility(View.GONE);
                    ivTogglePersonalizacion.setRotation(0);
                } else {
                    seccionPersonalizacion.setVisibility(View.VISIBLE);
                    ivTogglePersonalizacion.setRotation(180);
                }
            });
        } else {
            triggerPersonalizacion.setVisibility(View.GONE);
            seccionPersonalizacion.setVisibility(View.GONE);
        }
        configurarListenersDeCalculo();
        actualizarPrecio();
    }

    // --- Generador de UI Dinámica (El Robot) ---
    private void dibujarOpcionesDinamicas(String jsonString) {
        contenedorOpciones.removeAllViews();
        try {
            JSONArray grupos = new JSONArray(jsonString);

            // Color para los controles (Naranja de la marca)
            int colorMarca = Color.parseColor("#FFA857");
            ColorStateList colorStateList = ColorStateList.valueOf(colorMarca);

            for (int i = 0; i < grupos.length(); i++) {
                JSONObject grupo = grupos.getJSONObject(i);
                String titulo = grupo.getString("titulo");
                String tipo = grupo.getString("tipo");
                JSONArray opciones = grupo.getJSONArray("opciones");

                // 1. TÍTULO DEL GRUPO (Más grande y separado)
                TextView txtTitulo = new TextView(this);
                txtTitulo.setText(titulo);
                txtTitulo.setTextSize(18); // Más grande
                txtTitulo.setTypeface(null, Typeface.BOLD);
                txtTitulo.setTextColor(Color.parseColor("#333333")); // Gris oscuro elegante

                LinearLayout.LayoutParams paramsTitulo = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsTitulo.setMargins(0, 40, 0, 16); // Margen superior amplio
                txtTitulo.setLayoutParams(paramsTitulo);

                contenedorOpciones.addView(txtTitulo);

                if (tipo.equals("RADIO")) {
                    RadioGroup rg = new RadioGroup(this);
                    rg.setOrientation(RadioGroup.VERTICAL);
                    rg.setTag(titulo);

                    for (int j = 0; j < opciones.length(); j++) {
                        JSONObject op = opciones.getJSONObject(j);
                        String nombre = op.getString("nombre");
                        double precio = op.optDouble("precio", 0.0);

                        RadioButton rb = new RadioButton(this);

                        // ID necesario para que funcione la selección única
                        rb.setId(View.generateViewId());

                        // Texto con precio
                        String texto = nombre;
                        if (precio > 0) texto += "  (+ S/ " + String.format("%.2f", precio) + ")";
                        rb.setText(texto);

                        // --- ESTILOS VISUALES ---
                        rb.setTextSize(16);
                        rb.setTextColor(Color.parseColor("#555555"));
                        rb.setPadding(20, 20, 20, 20); // Espaciado interno
                        rb.setButtonTintList(colorStateList); // Círculo naranja

                        // Layout params para espaciado entre opciones
                        RadioGroup.LayoutParams paramsRb = new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        paramsRb.setMargins(0, 8, 0, 8);
                        rb.setLayoutParams(paramsRb);

                        // Guardamos precio
                        rb.setTag(precio);

                        if (j == 0) rb.setChecked(true);

                        rb.setOnCheckedChangeListener((v, isChecked) -> {
                            if(isChecked) recalcularPrecioDinamico();
                        });
                        rg.addView(rb);
                    }
                    contenedorOpciones.addView(rg);

                } else if (tipo.equals("CHECKBOX")) {
                    for (int j = 0; j < opciones.length(); j++) {
                        JSONObject op = opciones.getJSONObject(j);
                        String nombre = op.getString("nombre");
                        double precio = op.optDouble("precio", 0.0);

                        CheckBox cb = new CheckBox(this);
                        String texto = nombre;
                        if (precio > 0) texto += "  (+ S/ " + String.format("%.2f", precio) + ")";
                        cb.setText(texto);

                        // --- ESTILOS VISUALES ---
                        cb.setTextSize(16);
                        cb.setTextColor(Color.parseColor("#555555"));
                        cb.setPadding(20, 20, 20, 20);
                        cb.setButtonTintList(colorStateList); // Check naranja

                        LinearLayout.LayoutParams paramsCb = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramsCb.setMargins(0, 8, 0, 8);
                        cb.setLayoutParams(paramsCb);

                        cb.setTag(precio);
                        cb.setContentDescription(nombre);

                        cb.setOnCheckedChangeListener((v, isChecked) -> recalcularPrecioDinamico());
                        contenedorOpciones.addView(cb);
                    }
                }

                // Divisor sutil después de cada grupo (opcional)
                if (i < grupos.length() - 1) {
                    View divisor = new View(this);
                    divisor.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    LinearLayout.LayoutParams paramsDiv = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 2);
                    paramsDiv.setMargins(0, 30, 0, 0);
                    divisor.setLayoutParams(paramsDiv);
                    contenedorOpciones.addView(divisor);
                }
            }
            recalcularPrecioDinamico();

        } catch (JSONException e) {
            Log.e(TAG, "Error dibujando opciones: " + e.getMessage());
        }
    }

    private void recalcularPrecioDinamico() {
        double suma = 0.0;

        for (int i = 0; i < contenedorOpciones.getChildCount(); i++) {
            View v = contenedorOpciones.getChildAt(i);

            if (v instanceof RadioGroup) {
                RadioGroup rg = (RadioGroup) v;
                int idSeleccionado = rg.getCheckedRadioButtonId();
                if (idSeleccionado != -1) {
                    View rb = rg.findViewById(idSeleccionado);
                    if (rb.getTag() != null) suma += (double) rb.getTag();
                }
            } else if (v instanceof CheckBox) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked() && cb.getTag() != null) {
                    suma += (double) cb.getTag();
                }
            }
        }

        precioExtras = suma;
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

        btnAnadirCarrito.setOnClickListener(v -> enviarCarritoALaAPI());
    }

    private void actualizarPrecio() {
        if (productoActual == null) return;

        double precioUnitario = precioBase + precioExtras;
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

        // 1. Recolectar Opciones
        JSONObject personalizaciones = new JSONObject();
        try {
            if (productoActual.getPermite_personalizacion() == 1) {
                for (int i = 0; i < contenedorOpciones.getChildCount(); i++) {
                    View v = contenedorOpciones.getChildAt(i);

                    if (v instanceof RadioGroup) {
                        RadioGroup rg = (RadioGroup) v;
                        String tituloGrupo = (String) rg.getTag();
                        int idSel = rg.getCheckedRadioButtonId();
                        if (idSel != -1) {
                            RadioButton rb = rg.findViewById(idSel);
                            String texto = rb.getText().toString().split("\\(")[0].trim();
                            personalizaciones.put(tituloGrupo, texto);
                        }
                    } else if (v instanceof CheckBox) {
                        CheckBox cb = (CheckBox) v;
                        if (cb.isChecked()) {
                            String texto = cb.getText().toString().split("\\(")[0].trim();
                            String extras = personalizaciones.optString("Extras", "");
                            if (!extras.isEmpty()) extras += ", ";
                            extras += texto;
                            personalizaciones.put("Extras", extras);
                        }
                    }
                }
            }
        } catch (JSONException e) { e.printStackTrace(); }

        double precioUnitarioFinal = precioBase + precioExtras;

        JSONObject body = new JSONObject();
        try {
            body.put("id_cliente", idCliente);
            body.put("id_producto", productoActual.getId_producto());
            body.put("cantidad", cantidadActual);
            body.put("precio_unitario_final", precioUnitarioFinal);
            body.put("personalizaciones", personalizaciones.toString());
        } catch (JSONException e) { e.printStackTrace(); }

        // USAMOS ApiConfig.BASE_URL
        String URL_ADD = ApiConfig.BASE_URL + "api_agregar_carritoFCN";
        Log.d(TAG, "Enviando carrito: " + body.toString());

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
                    String mensajeError = "Error de conexión";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String bodyError = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e(TAG, "Error Volley Body: " + bodyError);
                            mensajeError += ": " + bodyError;
                        } catch (Exception e) {}
                    }
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