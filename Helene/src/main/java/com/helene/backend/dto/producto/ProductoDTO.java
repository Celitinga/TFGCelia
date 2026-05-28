package com.helene.backend.dto.producto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precioOriginal;
    private Double precioFinal;
    private Integer descuento;
    private String descripcion;
    private String categoria;
    private Long categoriaId;
    private String imagenUrl;
}
