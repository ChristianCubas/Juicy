package com.example.juicy.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarritoResponse {

    @SerializedName("id_cliente")
    private int idCliente;
    @SerializedName("id_venta")
    private int idVenta;
    @SerializedName("total_general")
    private double totalGeneral;
    @SerializedName("productos")
    private List<Producto> productos;

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public double getTotalGeneral() {
        return totalGeneral;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public static class Producto {
        @SerializedName("id_detalle")
        private int idDetalle;
        @SerializedName("nombre_producto")
        private String nombreProducto;
        @SerializedName("personalizaciones")
        private String personalizaciones;
        @SerializedName("cantidad")
        private int cantidad;
        @SerializedName("precio_unitario")
        private double precioUnitario;
        @SerializedName("precio_total")
        private double precioTotal;
        @SerializedName("imagen_url")
        private String imagenUrl;

        public int getIdDetalle() {
            return idDetalle;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public String getPersonalizaciones() {
            return personalizaciones;
        }

        public int getCantidad() {
            return cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public double getPrecioTotal() {
            return precioTotal;
        }

        public String getImagenUrl() {
            return imagenUrl;
        }
    }
}
