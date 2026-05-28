package com.helene.backend.dto.carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoRespuestaDTO {

    private Long id;

    private Long productoId;

    private String nombreProducto;

    private Double precioProducto;

    private Integer cantidad;

    private Double subtotal;

    private String imagenUrl;
}
