package com.helene.backend.dto.producto;

import lombok.Data;

import java.util.List;

@Data
public class DescuentoMasivoDTO {
    private List<Long> ids;
    private Integer descuento;
}

