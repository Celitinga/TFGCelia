package com.helene.backend.dto.carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link com.helene.backend.entity.Carrito}
 */
@AllArgsConstructor
@Data
public class CarritoDTO implements Serializable {
    private final Long productoId;
    private final Integer cantidad;
}