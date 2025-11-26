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
        @SerializedName("nombre_producto")
        private String nombreProducto;
        @SerializedName("tipo")
        private String tipo;
        @SerializedName("cantidad")
        private int cantidad;
        @SerializedName("precio_total")
        private double precioTotal;

        public String getNombreProducto() {
            return nombreProducto;
        }

        public String getTipo() {
            return tipo;
        }

        public int getCantidad() {
            return cantidad;
        }

        public double getPrecioTotal() {
            return precioTotal;
        }
    }
}
