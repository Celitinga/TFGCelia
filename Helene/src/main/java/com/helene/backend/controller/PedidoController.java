package com.helene.backend.controller;

import com.helene.backend.dto.pedido.CrearPedidoRequestDTO;
import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.backend.enums.EstadoPedido;
import com.helene.backend.service.IPedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private IPedidoService pedidoService;

    @PostMapping("/carrito/{usuarioId}")
    public ResponseEntity<?> crearDesdeCarrito(@PathVariable Long usuarioId) {
        log.info("Creando pedido desde carrito para usuarioId={}", usuarioId);
        try {
            PedidoDTO pedido = pedidoService.crearPedido(usuarioId);
            log.info("Pedido '{}' creado desde carrito para usuarioId={}", pedido.getNumeroPedido(), usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido creado correctamente");
            response.put("pedidoId", pedido.getId());
            response.put("numeroPedido", pedido.getNumeroPedido());
            response.put("fechaCreacion", pedido.getFechaCreacion());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al crear pedido desde carrito para usuarioId={}: {}", usuarioId, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearPedidoCompleto(@RequestBody CrearPedidoRequestDTO request) {
        log.info("Creando pedido completo para usuarioId={}, método de pago='{}'",
                request.getUsuarioId(), request.getMetodoPago());
        try {
            PedidoDTO pedido = pedidoService.crearPedidoCompleto(request);
            log.info("Pedido '{}' creado correctamente — usuarioId={}, total={}€",
                    pedido.getNumeroPedido(), request.getUsuarioId(), pedido.getTotal());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido creado correctamente");
            response.put("pedidoId", pedido.getId());
            response.put("numeroPedido", pedido.getNumeroPedido());
            response.put("estado", pedido.getEstado());
            response.put("fechaCreacion", pedido.getFechaCreacion());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al crear pedido completo para usuarioId={}: {}", request.getUsuarioId(), e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("Obteniendo pedidos del usuarioId={}", usuarioId);
        List<PedidoDTO> pedidos = pedidoService.listarPedidos(usuarioId);
        log.info("Se devuelven {} pedidos para usuarioId={}", pedidos.size(), usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id) {
        log.info("Obteniendo pedido con id={}", id);
        try {
            PedidoDTO pedido = pedidoService.obtenerPedido(id);
            log.info("Pedido id={} encontrado: número='{}'", id, pedido.getNumeroPedido());
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            log.error("Pedido id={} no encontrado: {}", id, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoDTO> obtenerPedidoPorNumero(@PathVariable String numeroPedido) {
        log.info("Obteniendo pedido por número='{}'", numeroPedido);
        try {
            PedidoDTO pedido = pedidoService.obtenerPedidoPorNumero(numeroPedido);
            log.info("Pedido '{}' encontrado con id={}", numeroPedido, pedido.getId());
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            log.error("Pedido número='{}' no encontrado: {}", numeroPedido, e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        log.info("Obteniendo todos los pedidos del sistema");
        List<PedidoDTO> pedidos = pedidoService.obtenerTodosPedidos();
        log.info("Total de pedidos en el sistema: {}", pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> listarPorEstado(@PathVariable EstadoPedido estado) {
        log.info("Obteniendo pedidos con estado='{}'", estado);
        List<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorEstado(estado);
        log.info("Se devuelven {} pedidos con estado='{}'", pedidos.size(), estado);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        log.info("Actualizando estado del pedido id={} a '{}'", id, estado);
        try {
            PedidoDTO pedido = pedidoService.actualizarEstado(id, estado);
            log.info("Estado del pedido id={} actualizado correctamente a '{}'", id, estado);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado correctamente");
            response.put("nuevoEstado", pedido.getEstado());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al actualizar estado del pedido id={}: {}", id, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@PathVariable Long id,
                                           @RequestBody Map<String, String> payload) {
        String paypalPaymentId = payload.get("paypalPaymentId");
        log.info("Confirmando pago del pedido id={}, paypalPaymentId='{}'", id, paypalPaymentId);
        try {
            PedidoDTO pedido = pedidoService.confirmarPago(id, paypalPaymentId);
            log.info("Pago confirmado correctamente para pedido id={}, nuevo estado='{}'",
                    id, pedido.getEstado());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pago confirmado correctamente");
            response.put("estado", pedido.getEstado());
            response.put("fechaEntrega", pedido.getFechaEntrega());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al confirmar pago del pedido id={}: {}", id, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id,
                                            @RequestBody Map<String, String> payload) {
        String motivo = payload.getOrDefault("motivo", "Cancelado por el usuario");
        log.info("Cancelando pedido id={}, motivo='{}'", id, motivo);
        try {
            PedidoDTO pedido = pedidoService.cancelarPedido(id, motivo);
            log.info("Pedido id={} cancelado correctamente", id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido cancelado correctamente");
            response.put("estado", pedido.getEstado());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al cancelar pedido id={}: {}", id, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}