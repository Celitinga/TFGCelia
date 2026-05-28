package com.helene.backend.service;

import com.helene.backend.dto.carrito.CarritoDTO;
import com.helene.backend.dto.carrito.CarritoRespuestaDTO;
import com.helene.backend.entity.Carrito;

import java.util.List;

public interface ICarritoService {

    List<CarritoRespuestaDTO> obtenerCarrito();

    void agregarProducto(CarritoDTO dto);

    void eliminarProducto(Long productoId);

    void vaciarCarrito();

    void actualizarCantidad(Long productoId, Integer cantidad);
}
