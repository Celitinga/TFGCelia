package com.helene.backend.dto.producto;

import lombok.Data;

@Data
public class CrearProductoDTO {

    private String nombre;
    private Double precioOriginal;
    private Integer descuento;
    private String descripcion;
    private Long categoriaId;
    private String imagenUrl;
}
