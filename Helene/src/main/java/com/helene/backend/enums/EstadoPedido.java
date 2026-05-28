package com.helene.backend.enums;

public enum EstadoPedido {
    PENDIENTE_PAGO("Pendiente de pago"),
    PAGADO("Pagado"),
    EN_PREPARACION("En preparación"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado"),
    DEVUELTO("Devuelto");

    private final String descripcion;

    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}