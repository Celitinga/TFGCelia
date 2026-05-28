package com.example.heleneapp;

public class Producto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precioFinal;
    private String imagenUrl;
    private Long categoriaId;
    private int descuento;

    public Producto() {}

    public Producto(Long id, String nombre, String descripcion, Double precioFinal, String imagenUrl, Long categoriaId, int descuento) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioFinal = precioFinal;
        this.imagenUrl = imagenUrl;
        this.categoriaId = categoriaId;
        this.descuento = descuento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(Double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }
}