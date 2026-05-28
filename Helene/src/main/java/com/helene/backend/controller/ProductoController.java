package com.helene.backend.controller;

import com.helene.backend.dto.producto.*;
import com.helene.backend.service.IProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private IProductoService productoService;

    @PostMapping
    public ProductoDTO crear(@RequestBody CrearProductoDTO dto) {
        log.info("Creando producto: '{}'", dto.getNombre());
        try {
            ProductoDTO creado = productoService.crearProducto(dto);
            log.info("Producto '{}' creado con id={}", creado.getNombre(), creado.getId());
            return creado;
        } catch (Exception e) {
            log.error("Error al crear producto '{}': {}", dto.getNombre(), e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ProductoDTO actualizar(@PathVariable Long id, @RequestBody ActualizarProductoDTO dto) {
        log.info("Actualizando producto id={}", id);
        try {
            ProductoDTO actualizado = productoService.actualizarProducto(id, dto);
            log.info("Producto id={} actualizado correctamente", id);
            return actualizado;
        } catch (Exception e) {
            log.error("Error al actualizar producto id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        log.info("Eliminando producto id={}", id);
        try {
            productoService.eliminarProducto(id);
            log.info("Producto id={} eliminado correctamente", id);
        } catch (Exception e) {
            log.error("Error al eliminar producto id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ProductoDTO obtener(@PathVariable Long id) {
        log.info("Obteniendo producto id={}", id);
        try {
            ProductoDTO producto = productoService.obtenerProducto(id);
            log.info("Producto id={} encontrado: '{}'", id, producto.getNombre());
            return producto;
        } catch (Exception e) {
            log.error("Producto id={} no encontrado: {}", id, e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<ProductoDTO> listar() {
        log.info("Listando todos los productos");
        List<ProductoDTO> productos = productoService.listarProductos();
        log.info("Se devuelven {} productos", productos.size());
        return productos;
    }

    @GetMapping("/categoria/{categoriaId}")
    public List<ProductoDTO> listarPorCategoria(@PathVariable Long categoriaId) {
        log.info("Listando productos de la categoría id={}", categoriaId);
        List<ProductoDTO> productos = productoService.listarPorCategoria(categoriaId);
        log.info("Se devuelven {} productos para categoría id={}", productos.size(), categoriaId);
        return productos;
    }

    @GetMapping("/ofertas")
    public List<ProductoDTO> listarOfertas() {
        log.info("Obteniendo productos en oferta");
        List<ProductoDTO> ofertas = productoService.listarOfertas();
        log.info("Se devuelven {} productos en oferta", ofertas.size());
        return ofertas;
    }

    @PatchMapping("/{id}/descuento")
    public ProductoDTO actualizarDescuento(@PathVariable Long id, @RequestBody ProductoDescuentoDTO dto) {
        log.info("Actualizando descuento del producto id={} a {}%", id, dto.getDescuento());
        try {
            ProductoDTO actualizado = productoService.actualizarDescuento(id, dto.getDescuento());
            log.info("Descuento del producto id={} actualizado a {}%", id, dto.getDescuento());
            return actualizado;
        } catch (Exception e) {
            log.error("Error al actualizar descuento del producto id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/buscar")
    public List<ProductoDTO> buscar(@RequestParam String nombre) {
        log.info("Búsqueda de productos por nombre='{}'", nombre);
        List<ProductoDTO> resultados = productoService.buscarPorNombre(nombre);
        log.info("Búsqueda '{}' devuelve {} resultados", nombre, resultados.size());
        return resultados;
    }

    @GetMapping("/orden/precio/asc")
    public List<ProductoDTO> ordenarAsc() {
        log.info("Listando productos ordenados por precio ascendente");
        List<ProductoDTO> productos = productoService.ordenarPorPrecioAsc();
        log.info("Se devuelven {} productos ordenados por precio ASC", productos.size());
        return productos;
    }

    @GetMapping("/orden/precio/desc")
    public List<ProductoDTO> ordenarDesc() {
        log.info("Listando productos ordenados por precio descendente");
        List<ProductoDTO> productos = productoService.ordenarPorPrecioDesc();
        log.info("Se devuelven {} productos ordenados por precio DESC", productos.size());
        return productos;
    }

    @GetMapping("/precio/rango")
    public List<ProductoDTO> filtrarRango(@RequestParam Double min, @RequestParam Double max) {
        log.info("Filtrando productos por rango de precio: {}€ - {}€", min, max);
        List<ProductoDTO> productos = productoService.filtrarPorRango(min, max);
        log.info("Se devuelven {} productos en el rango {}€-{}€", productos.size(), min, max);
        return productos;
    }

    @GetMapping("/barato")
    public ProductoDTO barato() {
        return productoService.productoMasBarato();
    }

    @GetMapping("/caro")
    public ProductoDTO caro() {
        return productoService.productoMasCaro();
    }

    @PatchMapping("/descuento/masivo")
    public void aplicarDescuentoMasivo(@RequestBody DescuentoMasivoDTO dto) {
        log.info("Aplicando descuento masivo del {}% a {} productos",
                dto.getDescuento(), dto.getIds().size());
        try {
            productoService.aplicarDescuentoMasivo(dto.getIds(), dto.getDescuento());
            log.info("Descuento masivo aplicado correctamente");
        } catch (Exception e) {
            log.error("Error al aplicar descuento masivo: {}", e.getMessage());
            throw e;
        }
    }

    @PatchMapping("/descuento/limpiar")
    public void limpiarDescuentos() {
        log.info("Limpiando todos los descuentos de productos");
        try {
            productoService.limpiarDescuentos();
            log.info("Descuentos eliminados correctamente de todos los productos");
        } catch (Exception e) {
            log.error("Error al limpiar descuentos: {}", e.getMessage());
            throw e;
        }
    }
}