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
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.Producto;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.Model.ValoracionProductoRequest;
import com.example.juicy.R;
import com.example.juicy.network.ApiConfig;
import com.example.juicy.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    // ------ Rating Section ------
    private LinearLayout triggerValoracion, seccionValoracion;
    private ImageView ivToggleValoracion;
    private ImageView[] estrellas;
    private Button btnEnviarValoracion;
    private EditText etComentario;
    private int valoracionSeleccionada = 0;
    private double ratingPromedio = 0.0;
    private int ratingTotal = 0;
    private int miRating = 0;
    private String miComentario = "";
    private TextView tvRatingResumen;

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

        // ----- Rating -----
        triggerValoracion = findViewById(R.id.triggerValoracion);
        seccionValoracion = findViewById(R.id.seccionValoracion);
        ivToggleValoracion = findViewById(R.id.ivToggleValoracion);
        btnEnviarValoracion = findViewById(R.id.btnEnviarValoracion);
        etComentario = findViewById(R.id.etComentario);
        tvRatingResumen = findViewById(R.id.tvRatingResumen);

        estrellas = new ImageView[]{
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };
    }

    private void cargarDetallesProducto() {
        String URL_API = ApiConfig.BASE_URL + "api_producto/" + idProducto;

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

                        // Configuración dinámica
                        String configJson = data.optString("config_personalizacion", null);
                        productoActual.setConfig_personalizacion(configJson);

                        // ----- Rating recibido del backend -----
                        ratingPromedio = data.optDouble("rating_promedio", 0.0);
                        ratingTotal = data.optInt("rating_total", 0);
                        if (!data.isNull("mi_rating")) {
                            miRating = data.optInt("mi_rating", 0);
                        } else {
                            miRating = 0;
                        }
                        miComentario = data.optString("mi_comentario", "");

                        if (ratingTotal > 0) {
                            tvRatingResumen.setText(
                                    String.format("Promedio %.1f de 5, basado en %d valoraciones",
                                            ratingPromedio, ratingTotal)
                            );
                        } else {
                            tvRatingResumen.setText("Aún sin valoraciones para este producto");
                        }

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
            if (!fullUrl.startsWith("http")) {
                fullUrl = ApiConfig.BASE_URL + fullUrl.replaceFirst("^/+", "");
            }
            imgProductoDetalle.setImageUrl(fullUrl, VolleySingleton.getInstance(this).getImageLoader());
        }

        // Personalización dinámica
        if (productoActual.getPermite_personalizacion() == 1) {
            triggerPersonalizacion.setVisibility(View.VISIBLE);

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
        configurarSeccionValoracion();
    }


    private void dibujarOpcionesDinamicas(String jsonString) {
        contenedorOpciones.removeAllViews();
        try {
            JSONArray grupos = new JSONArray(jsonString);

            int colorMarca = Color.parseColor("#FFA857");
            ColorStateList colorStateList = ColorStateList.valueOf(colorMarca);

            for (int i = 0; i < grupos.length(); i++) {
                JSONObject grupo = grupos.getJSONObject(i);
                String titulo = grupo.getString("titulo");
                String tipo = grupo.getString("tipo");
                JSONArray opciones = grupo.getJSONArray("opciones");

                TextView txtTitulo = new TextView(this);
                txtTitulo.setText(titulo);
                txtTitulo.setTextSize(18);
                txtTitulo.setTypeface(null, Typeface.BOLD);
                txtTitulo.setTextColor(Color.parseColor("#333333"));

                LinearLayout.LayoutParams paramsTitulo = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsTitulo.setMargins(0, 40, 0, 16);
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
                        rb.setId(View.generateViewId());

                        String texto = nombre;
                        if (precio > 0) texto += "  (+ S/ " + String.format("%.2f", precio) + ")";
                        rb.setText(texto);

                        rb.setTextSize(16);
                        rb.setTextColor(Color.parseColor("#555555"));
                        rb.setPadding(20, 20, 20, 20);
                        rb.setButtonTintList(colorStateList);

                        RadioGroup.LayoutParams paramsRb = new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                        paramsRb.setMargins(0, 8, 0, 8);
                        rb.setLayoutParams(paramsRb);

                        rb.setTag(precio);

                        if (j == 0) rb.setChecked(true);


                        rg.addView(rb);
                    }
                    rg.setOnCheckedChangeListener((group, checkedId) -> {
                        recalcularPrecioDinamico();
                    });
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

                        cb.setTextSize(16);
                        cb.setTextColor(Color.parseColor("#555555"));
                        cb.setPadding(20, 20, 20, 20);
                        cb.setButtonTintList(colorStateList);

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

    // ----------- VALORACIÓN ----------
    private void configurarSeccionValoracion() {

        triggerValoracion.setOnClickListener(v -> {
            if (seccionValoracion.getVisibility() == View.VISIBLE) {
                seccionValoracion.setVisibility(View.GONE);
                ivToggleValoracion.setRotation(0);
            } else {
                seccionValoracion.setVisibility(View.VISIBLE);
                ivToggleValoracion.setRotation(180);
            }
        });


        for (int i = 0; i < estrellas.length; i++) {
            final int index = i;
            estrellas[i].setOnClickListener(v -> {
                valoracionSeleccionada = index + 1; // 1..5
                actualizarEstrellasUI();
            });
        }

        // si el usuario ya tenía rating, precargar
        if (miRating > 0) {
            valoracionSeleccionada = miRating;
            actualizarEstrellasUI();
            if (miComentario != null && !miComentario.isEmpty()) {
                etComentario.setText(miComentario);
            }
        }

        // resumen
        if (ratingTotal > 0) {
            tvRatingResumen.setText(
                    String.format("Promedio %.1f de 5, basado en %d valoraciones",
                            ratingPromedio, ratingTotal)
            );
        } else {
            tvRatingResumen.setText("Aún sin valoraciones para este producto");
        }

        btnEnviarValoracion.setOnClickListener(v -> enviarValoracion());
    }

    private void enviarValoracion() {
        if (valoracionSeleccionada == 0) {
            Toast.makeText(this, "Selecciona de 1 a 5 estrellas", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String tokenJWTLocal = prefs.getString("tokenJWT", null);

        if (tokenJWTLocal == null) {
            Toast.makeText(this, "Inicia sesión para valorar", Toast.LENGTH_SHORT).show();
            return;
        }

        String comentario = etComentario.getText().toString().trim();

        ValoracionProductoRequest request = new ValoracionProductoRequest(
                productoActual.getId_producto(),
                valoracionSeleccionada,
                comentario
        );

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DambJuiceApi api = retrofit.create(DambJuiceApi.class);

        Call<RptaGeneral> call = api.valorarProducto("JWT " + tokenJWTLocal, request);

        call.enqueue(new Callback<RptaGeneral>() {
            @Override
            public void onResponse(Call<RptaGeneral> call, Response<RptaGeneral> response) {
                if (response.isSuccessful() && response.body() != null) {


                    Toast.makeText(DetalleProductoActivity.this,
                            "Valoración enviada correctamente", Toast.LENGTH_SHORT).show();

                    // 2. Leer los nuevos datos de rating que el backend devuelve
                    Object dataObj = response.body().getData();
                    if (dataObj instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) dataObj;

                        Object prom = map.get("rating_promedio");
                        if (prom instanceof Number) {
                            ratingPromedio = ((Number) prom).doubleValue();
                        }

                        Object total = map.get("rating_total");
                        if (total instanceof Number) {
                            ratingTotal = ((Number) total).intValue();
                        }

                        Object miRat = map.get("mi_rating");
                        if (miRat instanceof Number) {
                            miRating = ((Number) miRat).intValue();
                        }

                        Object miCom = map.get("mi_comentario");
                        if (miCom != null) {
                            miComentario = miCom.toString();
                        }
                    }

                    // 3. Actualizar el texto del resumen en pantalla
                    actualizarResumenValoracion();

                } else {
                    Toast.makeText(DetalleProductoActivity.this,
                            "Error al enviar la valoración", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RptaGeneral> call, Throwable t) {
                Toast.makeText(DetalleProductoActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void actualizarResumenValoracion() {
        tvRatingResumen.setText(String.format("Promedio %.1f de 5, basado en %d valoraciones",
                ratingPromedio, ratingTotal));
    }

    private void actualizarEstrellasUI() {
        for (int i = 0; i < estrellas.length; i++) {
            if (i < valoracionSeleccionada) {
                estrellas[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                estrellas[i].setImageResource(R.drawable.ic_star_outline);
            }
        }
    }
}
