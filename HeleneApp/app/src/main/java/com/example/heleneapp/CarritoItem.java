package com.example.heleneapp;

public class CarritoItem {

    private Long productoId;
    private String nombreProducto;
    private Double precioProducto;
    private Integer cantidad;
    private String imagenUrl;
    private int descuento;

    public CarritoItem() {}

    public CarritoItem(Long productoId, String nombreProducto, Double precioProducto, Integer cantidad, String imagenUrl, int descuento) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
        this.descuento = descuento;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(Double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public double getSubtotal() {
        return precioProducto * cantidad;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }
}