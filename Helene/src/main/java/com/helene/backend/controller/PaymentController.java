package com.helene.backend.controller;

import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.backend.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PayPalService payPalService;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, String>> createOrder(@RequestParam Long pedidoId) {
        log.info("Solicitud de creación de orden PayPal para pedidoId={}", pedidoId);
        try {
            Map<String, String> result = payPalService.createOrder(pedidoId);
            log.info("Orden PayPal creada correctamente para pedidoId={}, orderId='{}'",
                    pedidoId, result.get("orderId"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error al crear orden PayPal para pedidoId={}: {}", pedidoId, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/capture/{orderId}")
    public ResponseEntity<PedidoDTO> captureOrder(@PathVariable String orderId) {
        log.info("Solicitud de captura de pago PayPal — orderId='{}'", orderId);
        try {
            PedidoDTO pedido = payPalService.captureOrder(orderId);
            log.info("Pago capturado correctamente — orderId='{}', pedido número='{}'",
                    orderId, pedido.getNumeroPedido());
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            log.error("Error al capturar pago PayPal — orderId='{}': {}", orderId, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/create-order-direct")
    public ResponseEntity<Map<String, String>> createOrderDirect(@RequestBody Map<String, Object> body) {
        double amount      = ((Number) body.get("amount")).doubleValue();
        String currency    = (String) body.get("currency");
        String description = (String) body.get("description");

        log.info("Solicitud de orden PayPal directa — importe={} {}, descripción='{}'",
                amount, currency, description);
        try {
            Map<String, String> result = payPalService.createOrderDirect(amount, currency, description);
            log.info("Orden PayPal directa creada — orderId='{}'", result.get("orderId"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error al crear orden PayPal directa — importe={} {}: {}", amount, currency, e.getMessage());
            throw e;
        }
    }
}