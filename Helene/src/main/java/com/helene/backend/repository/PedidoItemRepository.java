package com.helene.backend.repository;

import com.helene.backend.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    List<PedidoItem> findByPedidoId(Long pedidoId);

    List<PedidoItem> findByProductoId(Long productoId);

    void deleteByPedidoId(Long pedidoId);
}

