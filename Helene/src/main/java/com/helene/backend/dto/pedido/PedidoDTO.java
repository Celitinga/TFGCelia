package com.helene.backend.dto.pedido;

import com.helene.backend.entity.DireccionEnvio;
import com.helene.backend.enums.EstadoPedido;
import com.helene.backend.enums.MetodoPago;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private String numeroPedido;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaEntrega;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private EstadoPedido estado;
    private MetodoPago metodoPago;
    private Double subtotal;
    private Double costeEnvio;
    private Double descuentoAplicado;
    private Double total;
    private String notas;
    private String paypalPaymentId;
    private DireccionEnvio direccionEnvio;
    private List<PedidoItemDTO> items;
}