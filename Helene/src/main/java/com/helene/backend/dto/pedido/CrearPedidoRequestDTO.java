package com.helene.backend.dto.pedido;

import com.helene.backend.entity.DireccionEnvio;
import com.helene.backend.enums.MetodoPago;
import lombok.Data;

import java.util.List;

@Data
public class CrearPedidoRequestDTO {

    private Long usuarioId;
    private DireccionEnvio direccionEnvio;
    private List<ItemPedidoRequestDTO> items;
    private Double subtotal;
    private Double costeEnvio;
    private Double descuentoAplicado;
    private Double total;
    private MetodoPago metodoPago;
    private String paypalPaymentId;
    private String notas;
}
