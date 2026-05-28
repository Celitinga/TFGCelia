package com.helene.backend.service;

import com.helene.backend.dto.producto.ActualizarProductoDTO;
import com.helene.backend.dto.producto.CrearProductoDTO;
import com.helene.backend.dto.producto.ProductoDTO;

import java.util.List;

public interface IProductoService {

    ProductoDTO crearProducto(CrearProductoDTO dto);

    ProductoDTO actualizarProducto(Long id, ActualizarProductoDTO dto);

    void eliminarProducto(Long id);

    ProductoDTO obtenerProducto(Long id);

    List<ProductoDTO> listarProductos();

    List<ProductoDTO> listarPorCategoria(Long categoriaId);

    List<ProductoDTO> listarOfertas();

    ProductoDTO actualizarDescuento(Long id, Integer descuento);

    List<ProductoDTO> buscarPorNombre(String nombre);

    List<ProductoDTO> ordenarPorPrecioAsc();

    List<ProductoDTO> ordenarPorPrecioDesc();

    List<ProductoDTO> filtrarPorRango(Double min, Double max);

    ProductoDTO productoMasBarato();

    ProductoDTO productoMasCaro();

    void aplicarDescuentoMasivo(List<Long> ids, Integer descuento);

    void limpiarDescuentos();

}
