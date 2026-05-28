package com.helene.backend.dto.pedido;

import lombok.Data;

@Data
public class ItemPedidoRequestDTO {
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Integer descuento;
}
