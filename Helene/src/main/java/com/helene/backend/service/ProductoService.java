package com.helene.backend.service;

import com.helene.backend.dto.producto.ActualizarProductoDTO;
import com.helene.backend.dto.producto.CrearProductoDTO;
import com.helene.backend.dto.producto.ProductoDTO;
import com.helene.backend.entity.Categoria;
import com.helene.backend.entity.Producto;
import com.helene.backend.exceptions.NotFoundEntityException;
import com.helene.backend.mapper.ProductoMapper;
import com.helene.backend.repository.CategoriaRepository;
import com.helene.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoMapper productoMapper;
    @Override
    public ProductoDTO crearProducto(CrearProductoDTO dto) {

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId()).orElseThrow(() -> new NotFoundEntityException("Categoría no encontrada"));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecioOriginal(dto.getPrecioOriginal());
        producto.setDescuento(dto.getDescuento());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoria(categoria);

        producto.aplicarDescuento();

        productoRepository.save(producto);

        return productoMapper.toDTO(producto);
    }

    @Override
    public ProductoDTO actualizarProducto(Long id, ActualizarProductoDTO dto) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Producto no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new NotFoundEntityException("Categoría no encontrada"));

        producto.setNombre(dto.getNombre());
        producto.setPrecioOriginal(dto.getPrecioOriginal());
        producto.setDescuento(dto.getDescuento());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagenUrl(dto.getImagenUrl());

        if (dto.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new NotFoundEntityException("Categoría no encontrada"));

            producto.setCategoria(categoria);
        }

        producto.aplicarDescuento();

        productoRepository.save(producto);

        return productoMapper.toDTO(producto);
    }

    @Override
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new NotFoundEntityException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    @Override
    public ProductoDTO obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .map(productoMapper::toDTO)
                .orElseThrow(() -> new NotFoundEntityException("Producto no encontrado"));
    }

    @Override
    public List<ProductoDTO> listarProductos() {
        return productoMapper.toDTOList(productoRepository.findAll());
    }

    @Override
    public List<ProductoDTO> listarPorCategoria(Long categoriaId) {
        return productoMapper.toDTOList(
                productoRepository.findByCategoriaId(categoriaId)
        );
    }

    @Override
    public List<ProductoDTO> listarOfertas() {
        return productoMapper.toDTOList(
                productoRepository.findAll().stream()
                        .filter(p -> p.getDescuento() != null && p.getDescuento() > 0)
                        .toList()
        );
    }

    @Override
    public ProductoDTO actualizarDescuento(Long id, Integer descuento) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException("Producto no encontrado"));

        producto.setDescuento(descuento != null ? descuento : 0);
        producto.aplicarDescuento();

        productoRepository.save(producto);

        return productoMapper.toDTO(producto);
    }

    @Override
    public void aplicarDescuentoMasivo(List<Long> ids, Integer descuento) {

        List<Producto> productos = productoRepository.findAllById(ids);

        productos.forEach(p -> {
            p.setDescuento(descuento);
            p.aplicarDescuento();
        });

        productoRepository.saveAll(productos);
    }

    @Override
    public void limpiarDescuentos() {

        List<Producto> productos = productoRepository.findAll();

        productos.forEach(p -> {
            p.setDescuento(0);
            p.aplicarDescuento();
        });

        productoRepository.saveAll(productos);
    }

    @Override
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoMapper.toDTOList(
                productoRepository.findByNombreContainingIgnoreCase(nombre)
        );
    }

    @Override
    public List<ProductoDTO> ordenarPorPrecioAsc() {
        return productoMapper.toDTOList(
                productoRepository.findAllByOrderByPrecioFinalAsc()
        );
    }

    @Override
    public List<ProductoDTO> ordenarPorPrecioDesc() {
        return productoMapper.toDTOList(
                productoRepository.findAllByOrderByPrecioFinalDesc()
        );
    }

    @Override
    public List<ProductoDTO> filtrarPorRango(Double min, Double max) {
        return productoMapper.toDTOList(
                productoRepository.findByPrecioFinalBetween(min, max)
        );
    }

    @Override
    public ProductoDTO productoMasBarato() {
        return productoRepository.findAll().stream()
                .min(Comparator.comparing(Producto::getPrecioFinal))
                .map(productoMapper::toDTO)
                .orElseThrow(() -> new NotFoundEntityException("No hay productos"));
    }

    @Override
    public ProductoDTO productoMasCaro() {
        return productoRepository.findAll().stream()
                .max(Comparator.comparing(Producto::getPrecioFinal))
                .map(productoMapper::toDTO)
                .orElseThrow(() -> new NotFoundEntityException("No hay productos"));
    }
}
