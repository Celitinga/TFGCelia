package com.example.heleneapp;

import java.util.List;

public class CrearPedidoRequest {
    private Long usuarioId;
    private DireccionEnvio direccionEnvio;
    private List<ItemPedidoRequest> items;
    private Double subtotal;
    private Double costeEnvio;
    private Double descuentoAplicado;
    private Double total;
    private String metodoPago;
    private String paypalPaymentId;
    private String notas;


    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public DireccionEnvio getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(DireccionEnvio direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public List<ItemPedidoRequest> getItems() { return items; }
    public void setItems(List<ItemPedidoRequest> items) { this.items = items; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getCosteEnvio() { return costeEnvio; }
    public void setCosteEnvio(Double costeEnvio) { this.costeEnvio = costeEnvio; }

    public Double getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(Double descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getPaypalPaymentId() { return paypalPaymentId; }
    public void setPaypalPaymentId(String paypalPaymentId) { this.paypalPaymentId = paypalPaymentId; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
