package com.helene.backend.service;

import com.helene.backend.dto.carrito.CarritoDTO;
import com.helene.backend.dto.carrito.CarritoRespuestaDTO;
import com.helene.backend.entity.Carrito;
import com.helene.backend.entity.Producto;
import com.helene.backend.entity.Usuario;
import com.helene.backend.exceptions.NotFoundEntityException;
import com.helene.backend.mapper.CarritoMapper;
import com.helene.backend.repository.CarritoRepository;
import com.helene.backend.repository.ProductoRepository;
import com.helene.backend.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoService implements ICarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoMapper carritoMapper;

    public CarritoService(CarritoRepository carritoRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository,
                          CarritoMapper carritoMapper) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.carritoMapper = carritoMapper;
    }

    @Override
    public List<CarritoRespuestaDTO> obtenerCarrito() {
        Long usuarioId = getUsuarioIdActual();

        return carritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(carritoMapper::toDTO)
                .toList();
    }

    @Override
    public void agregarProducto(CarritoDTO dto) {

        Long usuarioId = getUsuarioIdActual();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundEntityException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new NotFoundEntityException("Producto no encontrado"));

        Carrito existente = carritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(c -> c.getProducto().getId().equals(dto.getProductoId()))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + dto.getCantidad());
            carritoRepository.save(existente);
        } else {
            Carrito nuevo = new Carrito();
            nuevo.setUsuario(usuario);
            nuevo.setProducto(producto);
            nuevo.setCantidad(dto.getCantidad());

            carritoRepository.save(nuevo);
        }
    }

    @Override
    public void eliminarProducto(Long productoId) {

        Long usuarioId = getUsuarioIdActual();

        carritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(c -> c.getProducto().getId().equals(productoId))
                .findFirst()
                .ifPresent(carritoRepository::delete);
    }

    @Override
    public void vaciarCarrito() {
        carritoRepository.deleteByUsuarioId(getUsuarioIdActual());
    }

    @Override
    public void actualizarCantidad(Long productoId, Integer cantidad) {

        Long usuarioId = getUsuarioIdActual();

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .filter(c -> c.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new NotFoundEntityException("Producto no está en el carrito"));

        carrito.setCantidad(cantidad);
        carritoRepository.save(carrito);
    }

    private Long getUsuarioIdActual() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundEntityException("Usuario no encontrado"))
                .getId();
    }
}


