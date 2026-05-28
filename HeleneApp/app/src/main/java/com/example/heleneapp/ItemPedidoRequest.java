package com.example.heleneapp;

public class ItemPedidoRequest {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Integer descuento;

    public ItemPedidoRequest() {}

    public ItemPedidoRequest(Long productoId, Integer cantidad, Double precioUnitario, Integer descuento) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = descuento;
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getDescuento() { return descuento; }
    public void setDescuento(Integer descuento) { this.descuento = descuento; }
}
