package com.helene.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helene.backend.dto.pedido.PedidoDTO;
import com.helene.backend.entity.Pedido;
import com.helene.backend.enums.EstadoPedido;
import com.helene.backend.mapper.PedidoMapper;
import com.helene.backend.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.*;

@Service
@RequiredArgsConstructor
public class PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> createOrder(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + pedidoId));

        String accessToken = getAccessToken();

        Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "reference_id", pedido.getNumeroPedido(),
                        "description", "Pedido Helene #" + pedido.getNumeroPedido(),
                        "amount", Map.of(
                                "currency_code", "EUR",
                                "value", String.format(Locale.US, "%.2f", pedido.getTotal())
                        )
                )),
                "application_context", Map.of(
                        "return_url",    "heleneapp://paypalpay",
                        "cancel_url",    "heleneapp://paypalcancel",
                        "brand_name",    "Helene Cosmética Natural",
                        "landing_page",  "NO_PREFERENCE",
                        "user_action",   "PAY_NOW",
                        "locale",        "es-ES"
                )
        );

        try {
            String json = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(baseUrl + "/v2/checkout/orders")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    throw new RuntimeException("PayPal error: " + responseBody);
                }

                Map<?, ?> result = objectMapper.readValue(responseBody, Map.class);
                String orderId = (String) result.get("id");

                String approveUrl = extractApproveUrl(result);

                pedido.setPaypalPaymentId(orderId);
                pedidoRepository.save(pedido);

                return Map.of("orderId", orderId, "approveUrl", approveUrl);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al crear orden PayPal: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String extractApproveUrl(Map<?, ?> result) {
        List<Map<String, String>> links = (List<Map<String, String>>) result.get("links");
        if (links == null) throw new RuntimeException("PayPal no devolvió links");

        return links.stream()
                .filter(link -> "approve".equals(link.get("rel")))
                .map(link -> link.get("href"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró approve URL"));
    }

    public PedidoDTO captureOrder(String paypalOrderId) {

        String accessToken = getAccessToken();

        Request request = new Request.Builder()
                .url(baseUrl + "/v2/checkout/orders/" + paypalOrderId + "/capture")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create("{}", MediaType.get("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new RuntimeException("PayPal error capture: " + responseBody);
            }

            Map<?, ?> result = objectMapper.readValue(responseBody, Map.class);
            String status = (String) result.get("status");

            if (!"COMPLETED".equals(status)) {
                throw new RuntimeException("Pago no completado: " + status);
            }

            Pedido pedido = pedidoRepository.findByPaypalPaymentId(paypalOrderId)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + paypalOrderId));

            pedido.setEstado(EstadoPedido.PAGADO);
            pedidoRepository.save(pedido);

            return pedidoMapper.toDTO(pedido);

        } catch (IOException e) {
            throw new RuntimeException("Error capturando pago: " + e.getMessage());
        }
    }

    private String getAccessToken() {

        String credentials = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        Request request = new Request.Builder()
                .url(baseUrl + "/v1/oauth2/token")
                .addHeader("Authorization", "Basic " + encoded)
                .post(RequestBody.create(
                        "grant_type=client_credentials",
                        MediaType.get("application/x-www-form-urlencoded")
                ))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            String body = response.body().string();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Error token PayPal: " + body);
            }

            Map<?, ?> result = objectMapper.readValue(body, Map.class);
            return (String) result.get("access_token");

        } catch (IOException e) {
            throw new RuntimeException("Error red PayPal: " + e.getMessage());
        }
    }

    public Map<String, String> createOrderDirect(double amount, String currency, String description) {
        String accessToken = getAccessToken();

        Map<String, Object> body = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "description", description,
                        "amount", Map.of(
                                "currency_code", currency,
                                "value", String.format(Locale.US, "%.2f", amount)
                        )
                )),
                "application_context", Map.of(
                        "return_url",   "heleneapp://paypalpay",
                        "cancel_url",  "heleneapp://paypalcancel",
                        "brand_name",  "Helene Cosmética Natural",
                        "landing_page","NO_PREFERENCE",
                        "user_action", "PAY_NOW"
                )
        );

        try {
            String json = objectMapper.writeValueAsString(body);
            Request request = new Request.Builder()
                    .url(baseUrl + "/v2/checkout/orders")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                if (!response.isSuccessful())
                    throw new RuntimeException("PayPal error: " + responseBody);

                Map<?, ?> result = objectMapper.readValue(responseBody, Map.class);
                String orderId = (String) result.get("id");
                String approveUrl = extractApproveUrl(result);

                return Map.of("orderId", orderId, "approveUrl", approveUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al crear orden PayPal: " + e.getMessage());
        }
    }
}