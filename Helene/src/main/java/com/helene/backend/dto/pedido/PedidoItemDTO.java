package com.helene.backend.dto.pedido;

import lombok.Data;

@Data
public class PedidoItemDTO {
    private Long id;
    private Long pedidoId;
    private Long productoId;
    private String productoNombre;
    private String productoImagen;
    private Integer cantidad;
    private Double precioUnitario;
    private Double descuentoAplicado;
    private Double subtotal;
}