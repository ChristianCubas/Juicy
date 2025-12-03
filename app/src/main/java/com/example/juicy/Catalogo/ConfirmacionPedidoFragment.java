package com.example.juicy.Catalogo;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.juicy.Interface.DambJuiceApi;
import com.example.juicy.Model.RptaGeneral;
import com.example.juicy.databinding.FragmentConfirmacionPedidoBinding;
import com.example.juicy.network.RetrofitClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmacionPedidoFragment extends Fragment {

    public static final String ARG_ID_VENTA = "id_venta";
    public static final String ARG_TOTAL = "total_pedido";
    public static final String ARG_DIRECCION = "direccion_texto";
    public static final String ARG_METODO = "metodo_pago";

    private FragmentConfirmacionPedidoBinding binding;

    private int idVenta;
    private double total;
    private String direccion;
    private String metodoPago;

    private boolean descargandoPdf = false;
    private boolean enviandoCorreo = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmacionPedidoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leerArgumentos();
        dibujarInfo();
        binding.btnDescargarPdf.setOnClickListener(v -> descargarPdf());
        binding.btnEnviarCorreo.setOnClickListener(v -> enviarPorCorreo());
    }

    private void leerArgumentos() {
        Bundle args = getArguments();
        if (args == null) args = Bundle.EMPTY;
        idVenta = args.getInt(ARG_ID_VENTA, 0);
        total = args.getDouble(ARG_TOTAL, 0);
        direccion = args.getString(ARG_DIRECCION, "Dirección no disponible");
        metodoPago = args.getString(ARG_METODO, "Método no disponible");
    }

    private void dibujarInfo() {
        Locale localePE = new Locale("es", "PE");
        NumberFormat format = NumberFormat.getCurrencyInstance(localePE);

        binding.tvNumeroPedido.setText("Pedido #" + idVenta);
        binding.tvTotal.setText("Total: " + format.format(total));
        binding.tvDireccionEntrega.setText("Dirección: " + direccion);
        binding.tvMetodoPago.setText("Método de pago: " + metodoPago);
    }


    private void descargarPdf() {
        if (descargandoPdf) return;
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Sesión expirada. Inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idVenta <= 0) {
            Toast.makeText(requireContext(), "Venta inválida para generar PDF.", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            Toast.makeText(requireContext(), "No se pudo acceder al gestor de descargas.", Toast.LENGTH_SHORT).show();
            return;
        }

        descargandoPdf = true;
        binding.btnDescargarPdf.setEnabled(false);
        binding.btnDescargarPdf.setText("Generando PDF...");

        Uri uri = Uri.parse(RetrofitClient.getBaseUrl() + "api_venta_pdf/" + idVenta);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.addRequestHeader("Authorization", "JWT " + token.trim());
        request.setTitle("Comprobante Juicy #" + idVenta);
        request.setDescription("Descargando comprobante en PDF");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "pedido_" + idVenta + ".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadManager.enqueue(request);
        Toast.makeText(requireContext(), "Descarga iniciada. Revisa la carpeta Descargas.", Toast.LENGTH_LONG).show();

        descargandoPdf = false;
        binding.btnDescargarPdf.setEnabled(true);
        binding.btnDescargarPdf.setText("Generar comprobante (PDF)");
    }

    private void enviarPorCorreo() {
        if (enviandoCorreo) return;
        SharedPreferences prefs = requireActivity().getSharedPreferences("SP_JUICY", Context.MODE_PRIVATE);
        String token = prefs.getString("tokenJWT", null);
        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Sesión expirada. Inicie sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idVenta <= 0) {
            Toast.makeText(requireContext(), "Venta inválida para enviar comprobante.", Toast.LENGTH_SHORT).show();
            return;
        }

        enviandoCorreo = true;
        binding.btnEnviarCorreo.setEnabled(false);
        binding.btnEnviarCorreo.setText("Enviando...");

        RetrofitClient.getApiService()
                .enviarComprobanteCorreo("JWT " + token.trim(), idVenta)
                .enqueue(new Callback<RptaGeneral>() {
                    @Override
                    public void onResponse(@NonNull Call<RptaGeneral> call,
                                           @NonNull Response<RptaGeneral> response) {
                        enviandoCorreo = false;
                        binding.btnEnviarCorreo.setEnabled(true);
                        binding.btnEnviarCorreo.setText("Enviar comprobante al correo");

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "No se pudo enviar el correo (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(requireContext(),
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<RptaGeneral> call,
                                          @NonNull Throwable t) {
                        enviandoCorreo = false;
                        binding.btnEnviarCorreo.setEnabled(true);
                        binding.btnEnviarCorreo.setText("Enviar comprobante al correo");
                        Toast.makeText(requireContext(),
                                "Error al enviar correo: " + t.getMessage(),
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
