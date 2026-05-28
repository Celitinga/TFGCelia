package com.helene.backend.repository;

import com.helene.backend.entity.Pedido;
import com.helene.backend.entity.Usuario;
import com.helene.backend.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    Page<Pedido> findByUsuario(Usuario usuario, Pageable pageable);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByEstadoAndUsuarioId(EstadoPedido estado, Long usuarioId);

    List<Pedido> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<Pedido> findByPaypalPaymentId(String paypalPaymentId);

    @Query("SELECT p FROM Pedido p WHERE p.estado = 'PENDIENTE_PAGO' AND p.fechaCreacion < :fechaLimite")
    List<Pedido> findPedidosPendientesPago(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT DATE(p.fechaCreacion), SUM(p.total) FROM Pedido p WHERE p.estado = 'PAGADO' GROUP BY DATE(p.fechaCreacion)")
    List<Object[]> findVentasDiarias();

    @Query("SELECT p.estado, COUNT(p) FROM Pedido p GROUP BY p.estado")
    List<Object[]> countPedidosByEstado();

    @Query("SELECT YEAR(p.fechaCreacion), MONTH(p.fechaCreacion), SUM(p.total) FROM Pedido p WHERE p.estado = 'PAGADO' GROUP BY YEAR(p.fechaCreacion), MONTH(p.fechaCreacion)")
    List<Object[]> findVentasPorMes();
}

