package com.helene.backend.repository.jooq;

import com.helene.backend.dto.pedido.PedidoDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IPedidoJooqRepository {
    List<PedidoDTO> findPedidosPendientesPago(LocalDateTime fechaLimite);

    List<Object[]> findVentasDiarias();

    List<Object[]> countPedidosByEstado();

    List<Object[]> findVentasPorMes();
}
