package com.example.heleneapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.heleneapp.network.ApiClient;
import java.util.Map;

public class PayPalHelper {

        public interface PayPalPaymentListener {
            void onPaymentSuccess(String orderId);
            void onPaymentCanceled();
            void onPaymentError(String error);
        }

        private final Activity activity;
        private final ApiClient apiClient;
        private PayPalPaymentListener listener;

        private static final String PREFS = "paypal_prefs";
        private static final String KEY_ORDER = "pending_order_id";

        public PayPalHelper(Activity activity, ApiClient apiClient) {
            this.activity = activity;
            this.apiClient = apiClient;
        }

        public void startPayment(Long pedidoId, PayPalPaymentListener listener) {
            this.listener = listener;

            apiClient.createPayPalOrder(pedidoId, new ApiClient.ApiCallback<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> response) {
                    String orderId = (String) response.get("orderId");
                    if (orderId == null) {
                        listener.onPaymentError("El servidor no devolvió un orderId");
                        return;
                    }
                    savePendingOrder(orderId);
                    openPayPalInBrowser(orderId);
                }

                @Override
                public void onError(String error) {
                    listener.onPaymentError("Error al iniciar pago: " + error);
                }
            });
        }

        public void capturePayment(PayPalPaymentListener listener) {
            this.listener = listener;

            String orderId = getPendingOrderId();
            if (orderId == null) {
                listener.onPaymentError("No hay pago pendiente");
                return;
            }

            apiClient.capturePayPalOrder(orderId, new ApiClient.ApiCallback<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> response) {
                    clearPendingOrder();
                    if (listener != null) listener.onPaymentSuccess(orderId);
                }

                @Override
                public void onError(String error) {
                    if (listener != null) listener.onPaymentError("Error al capturar: " + error);
                }
            });
        }

        private void openPayPalInBrowser(String orderId) {
            String url = "https://www.sandbox.paypal.com/checkoutnow?token=" + orderId;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        }

        private void savePendingOrder(String orderId) {
            activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().putString(KEY_ORDER, orderId).apply();
        }

        public String getPendingOrderId() {
            return activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getString(KEY_ORDER, null);
        }

        public void clearPendingOrder() {
            activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit().remove(KEY_ORDER).apply();
        }

        public void handlePossibleCancel() {
            if (getPendingOrderId() != null && listener != null) {
                listener.onPaymentCanceled();
                clearPendingOrder();
            }
        }
    }