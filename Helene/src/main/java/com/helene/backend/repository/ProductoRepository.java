package com.helene.backend.repository;

import com.helene.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findAllByOrderByPrecioFinalAsc();

    List<Producto> findAllByOrderByPrecioFinalDesc();

    List<Producto> findByPrecioFinalBetween(Double min, Double max);

}
