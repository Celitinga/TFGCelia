package com.helene.backend.service;

import com.helene.backend.dto.pedido.CrearPedidoRequestDTO;
import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.backend.enums.EstadoPedido;

import java.util.List;

public interface IPedidoService {
    PedidoDTO crearPedido(Long usuarioId);
    List<PedidoDTO> listarPedidos(Long usuarioId);
    PedidoDTO crearPedidoCompleto(CrearPedidoRequestDTO request);
    PedidoDTO actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado);
    PedidoDTO confirmarPago(Long pedidoId, String paypalPaymentId);
    PedidoDTO cancelarPedido(Long pedidoId, String motivo);
    PedidoDTO obtenerPedido(Long id);
    PedidoDTO obtenerPedidoPorNumero(String numeroPedido);
    List<PedidoDTO> obtenerPedidosPorEstado(EstadoPedido estado);
    List<PedidoDTO> obtenerTodosPedidos();
}
