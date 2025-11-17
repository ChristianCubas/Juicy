package com.example.juicy.Catalogo; // Asegúrate que sea tu paquete correcto

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
import com.android.volley.Request;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.juicy.Model.Producto;
import com.example.juicy.R;
import com.example.juicy.network.VolleySingleton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleProductoActivity extends AppCompatActivity {

    private static final String TAG = "Depuracion"; // <-- TAG PARA DEPURACIÓN

    // --- Vistas de la UI ---
    private ImageButton btnAtras;
    private NetworkImageView imgProductoDetalle;
    private TextView tvNombreDetalle, tvDescripcionDetalle, tvPrecioDetalle, tvCantidad, tvTotalFinal; // <-- tvTotalFinal AÑADIDO
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
        tvPrecioDetalle = findViewById(R.id.tvPrecioDetalle); // <-- El de arriba (será estático)
        tvTotalFinal = findViewById(R.id.tvTotalFinal);     // <-- El del footer (será dinámico)
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
        String URL_API = "https://grupotres20252.pythonanywhere.com/api_producto/" + idProducto;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, URL_API, null,
                response -> {
                    try {
                        if (response.getInt("code") != 1) {
                            Log.e(TAG, "Respuesta no OK de la API: " + response.getString("message"));
                            Toast.makeText(this, "Error del servidor", Toast.LENGTH_SHORT).show();
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

                        Log.d(TAG, "Producto cargado: " + productoActual.getNombre());
                        Log.d(TAG, "Precio Regular: " + productoActual.getPrecio_extra_regular());
                        Log.d(TAG, "Precio Alto: " + productoActual.getPrecio_extra_alto());

                        configurarUI();

                    } catch (Exception e) {
                        Log.e(TAG, "Error al parsear JSON: " + e.getMessage());
                        Toast.makeText(this, "Error al procesar los datos.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error de Volley: " + error.toString());
                    Toast.makeText(this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
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

        // --- 1. Llenar datos ---
        tvNombreDetalle.setText(productoActual.getNombre());
        tvDescripcionDetalle.setText(productoActual.getDescripcion());
        tvCantidad.setText(String.valueOf(cantidadActual));

        // --- ¡CAMBIO IMPORTANTE! ---
        // Fijamos el precio base en el TextView de arriba (como "Precio Regular S/ 10")
        tvPrecioDetalle.setText("S/ " + String.format("%.2f", productoActual.getPrecio()));

        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();
        imgProductoDetalle.setImageUrl(productoActual.getImagen_url(), imageLoader);

        // --- 2. Lógica de Personalización ---
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

            // --- 3. Opciones por Defecto ---
            toggleGroupTamaño.check(R.id.btnTamañoRegular); // Esto disparará el listener

            String azucarDefecto = productoActual.getNivel_azucar_defecto();
            if (azucarDefecto.equalsIgnoreCase("BAJO")) {
                toggleGroupAzucar.check(R.id.btnAzucarBajo);
            } else if (azucarDefecto.equalsIgnoreCase("CERO")) {
                toggleGroupAzucar.check(R.id.btnAzucarCero);
            } else {
                toggleGroupAzucar.check(R.id.btnAzucarNormal);
            }

        } else {
            triggerPersonalizacion.setVisibility(View.GONE);
            seccionPersonalizacion.setVisibility(View.GONE);
        }

        // --- 4. Configurar Listeners ---
        configurarListenersDeCalculo();

        // --- 5. Calcular precio inicial ---
        // Se llamará automáticamente por el .check() de toggleGroupTamaño
        // Pero lo llamamos de nuevo por si acaso y para la cantidad.
        actualizarPrecio();
    }

    private void configurarListenersDeCalculo() {

        btnRestarCantidad.setOnClickListener(v -> {
            Log.d(TAG, "Botón Restar Clickeado"); // <-- LOG
            if (cantidadActual > 1) {
                cantidadActual--;
                tvCantidad.setText(String.valueOf(cantidadActual));
                actualizarPrecio();
            }
        });

        btnSumarCantidad.setOnClickListener(v -> {
            Log.d(TAG, "Botón Sumar Clickeado"); // <-- LOG
            cantidadActual++;
            tvCantidad.setText(String.valueOf(cantidadActual));
            actualizarPrecio();
        });

        toggleGroupTamaño.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "Botón Tamaño Cambiado"); // <-- LOG
                actualizarPrecio();
            }
        });

        toggleGroupAzucar.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                Log.d(TAG, "Botón Azúcar Cambiado"); // <-- LOG
                // No afecta el precio, no llamamos a actualizarPrecio()
            }
        });

        btnAnadirCarrito.setOnClickListener(v -> {
            prepararYAnadirAlCarrito();
        });
    }

    private void actualizarPrecio() {
        if (productoActual == null) {
            Log.e(TAG, "actualizarPrecio() llamado pero productoActual es nulo.");
            return;
        }

        double precioBase = productoActual.getPrecio();
        double precioExtra = 0.0;

        int idTamañoSeleccionado = toggleGroupTamaño.getCheckedButtonId();

        if (idTamañoSeleccionado == R.id.btnTamañoSmall) {
            precioExtra = productoActual.getPrecio_extra_small();
        } else if (idTamañoSeleccionado == R.id.btnTamañoRegular) {
            precioExtra = productoActual.getPrecio_extra_regular();
        } else if (idTamañoSeleccionado == R.id.btnTamañoAlto) {
            precioExtra = productoActual.getPrecio_extra_alto();
        }

        double precioUnitario = precioBase + precioExtra;
        precioCalculado = precioUnitario * cantidadActual;

        // --- ¡CAMBIO IMPORTANTE! ---
        // Ya no tocamos tvPrecioDetalle (el de arriba)
        // Solo actualizamos el TOTAL del footer
        tvTotalFinal.setText("S/ " + String.format("%.2f", precioCalculado));

        // --- LOGS DE DEPURACIÓN ---
        Log.d(TAG, "--- Cálculo de Precio ---");
        Log.d(TAG, "Precio Base: " + precioBase);
        Log.d(TAG, "ID Tamaño: " + idTamañoSeleccionado);
        Log.d(TAG, "Precio Extra: " + precioExtra);
        Log.d(TAG, "Precio Unitario: " + precioUnitario);
        Log.d(TAG, "Cantidad: " + cantidadActual);
        Log.d(TAG, "TOTAL CALCULADO: " + precioCalculado);
        Log.d(TAG, "-------------------------");
    }

    private void prepararYAnadirAlCarrito() {

        String tamañoSeleccionado = "";
        int idTamaño = toggleGroupTamaño.getCheckedButtonId();
        if (idTamaño == R.id.btnTamañoSmall) tamañoSeleccionado = "Small";
        else if (idTamaño == R.id.btnTamañoRegular) tamañoSeleccionado = "Regular";
        else if (idTamaño == R.id.btnTamañoAlto) tamañoSeleccionado = "Alto";

        String azucarSeleccionado = "";
        int idAzucar = toggleGroupAzucar.getCheckedButtonId();
        if (idAzucar == R.id.btnAzucarCero) azucarSeleccionado = "Cero";
        else if (idAzucar == R.id.btnAzucarBajo) azucarSeleccionado = "Bajo";
        else if (idAzucar == R.id.btnAzucarNormal) azucarSeleccionado = "Normal";

        double precioUnitarioFinal = precioCalculado / cantidadActual;

        // Crear el JSON que se enviará a la API

        // ... (Aquí irá la lógica para la API /api_anadir_carrito)

        String resumen = "Añadido:\n" +
                "Producto: " + productoActual.getNombre() + "\n" +
                "Cantidad: " + cantidadActual + "\n" +
                "Total: S/ " + String.format("%.2f", precioCalculado);

        Toast.makeText(this, resumen, Toast.LENGTH_LONG).show();
    }
}
