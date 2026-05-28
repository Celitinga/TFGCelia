package com.helene.backend.enums;

public enum MetodoPago {
    PAYPAL("PayPal"),
    TARJETA_CREDITO("Tarjeta de crédito/débito"),
    CONTRAENTREGA("Pago contraentrega");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
