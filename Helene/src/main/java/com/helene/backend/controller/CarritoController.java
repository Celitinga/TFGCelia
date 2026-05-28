package com.helene.backend.controller;

import com.helene.backend.dto.carrito.CarritoDTO;
import com.helene.backend.dto.carrito.CarritoRespuestaDTO;
import com.helene.backend.service.ICarritoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private static final Logger log = LoggerFactory.getLogger(CarritoController.class);

    @Autowired
    private ICarritoService carritoService;

    @PostMapping
    public void agregar(@RequestBody CarritoDTO dto) {
        log.info("Agregando producto id={} al carrito, cantidad={}",
                dto.getProductoId(), dto.getCantidad());
        try {
            carritoService.agregarProducto(dto);
            log.info("Producto id={} agregado correctamente al carrito", dto.getProductoId());
        } catch (Exception e) {
            log.error("Error al agregar producto id={} al carrito: {}", dto.getProductoId(), e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{productoId}")
    public void eliminar(@PathVariable Long productoId) {
        log.info("Eliminando producto id={} del carrito", productoId);
        try {
            carritoService.eliminarProducto(productoId);
            log.info("Producto id={} eliminado del carrito correctamente", productoId);
        } catch (Exception e) {
            log.error("Error al eliminar producto id={} del carrito: {}", productoId, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping
    public void vaciar() {
        log.info("Vaciando el carrito completo");
        try {
            carritoService.vaciarCarrito();
            log.info("Carrito vaciado correctamente");
        } catch (Exception e) {
            log.error("Error al vaciar el carrito: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<CarritoRespuestaDTO> obtener() {
        log.info("Obteniendo contenido del carrito");
        List<CarritoRespuestaDTO> items = carritoService.obtenerCarrito();
        log.info("Carrito obtenido con {} items", items.size());
        return items;
    }

    @PutMapping("/{productoId}")
    public void actualizarCantidad(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        log.info("Actualizando cantidad del producto id={} a {} en el carrito", productoId, cantidad);
        try {
            carritoService.actualizarCantidad(productoId, cantidad);
            log.info("Cantidad del producto id={} actualizada a {}", productoId, cantidad);
        } catch (Exception e) {
            log.error("Error al actualizar cantidad del producto id={}: {}", productoId, e.getMessage());
            throw e;
        }
    }
}