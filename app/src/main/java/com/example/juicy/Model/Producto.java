package com.example.juicy.Model;

public class Producto {
    private int id_producto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private String imagen_url;
    private int stock;
    private int permite_personalizacion; // 1 = sí, 0 = no

    private String config_personalizacion;


    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getConfig_personalizacion() {
        return config_personalizacion;
    }

    public void setConfig_personalizacion(String config_personalizacion) {
        this.config_personalizacion = config_personalizacion;
    }

    public int getPermite_personalizacion() {
        return permite_personalizacion;
    }

    public void setPermite_personalizacion(int permite_personalizacion) {
        this.permite_personalizacion = permite_personalizacion;
    }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }
    public int getIdProducto() { return id_producto; }
    public void setIdProducto(int id_producto) { this.id_producto = id_producto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getImagen_url() { return imagen_url; }
    public void setImagen_url(String imagen_url) { this.imagen_url = imagen_url; }
}
