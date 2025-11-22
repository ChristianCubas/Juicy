package com.example.juicy.Model;

public class Producto {
    private int id_producto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;
    private String imagen_url;
    private int permite_personalizacion; // 1 = sí, 0 = no
    private double precio_extra_small;
    private double precio_extra_regular;
    private double precio_extra_alto;
    private String nivel_azucar_defecto;
    public int getPermite_personalizacion() {
        return permite_personalizacion;
    }

    public void setPermite_personalizacion(int permite_personalizacion) {
        this.permite_personalizacion = permite_personalizacion;
    }

    public double getPrecio_extra_small() {
        return precio_extra_small;
    }

    public void setPrecio_extra_small(double precio_extra_small) {
        this.precio_extra_small = precio_extra_small;
    }

    public double getPrecio_extra_regular() {
        return precio_extra_regular;
    }

    public void setPrecio_extra_regular(double precio_extra_regular) {
        this.precio_extra_regular = precio_extra_regular;
    }

    public double getPrecio_extra_alto() {
        return precio_extra_alto;
    }

    public void setPrecio_extra_alto(double precio_extra_alto) {
        this.precio_extra_alto = precio_extra_alto;
    }

    public String getNivel_azucar_defecto() {
        return nivel_azucar_defecto;
    }

    public void setNivel_azucar_defecto(String nivel_azucar_defecto) {
        this.nivel_azucar_defecto = nivel_azucar_defecto;
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
