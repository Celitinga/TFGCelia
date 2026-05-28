package com.example.heleneapp.network;

import android.os.Handler;
import android.os.Looper;

import com.example.heleneapp.CarritoItem;
import com.example.heleneapp.CrearPedidoRequest;
import com.example.heleneapp.Producto;
import com.example.heleneapp.UserSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = "http://44.219.11.148:5000/api";

    private final OkHttpClient client;
    private final Gson gson;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }

    public ApiClient() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }


    public void login(String username, String password, ApiCallback<Map<String, Object>> callback) {

        Map<String, String> credentials = Map.of("username", username, "password", password);

        String json = gson.toJson(credentials);

        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }

    public void register(String username, String email, String password,
                         ApiCallback<Map<String, Object>> callback) {

        Map<String, String> userData = Map.of(
                "username", username,
                "email", email,
                "password", password
        );

        String json = gson.toJson(userData);

        Request request = new Request.Builder()
                .url(BASE_URL + "/register")
                .post(RequestBody.create(
                        json,
                        MediaType.parse("application/json")
                ))
                .build();

        executeRequest(
                request,
                new TypeToken<Map<String, Object>>(){}.getType(),
                callback
        );
    }

    private Request.Builder addAuthHeader(Request.Builder builder) {

        String token = UserSession.getInstance().getToken();

        if (token != null && !token.isEmpty()) {builder.addHeader("Authorization", "Bearer " + token);}

        return builder;
    }

    public void getCarrito(ApiCallback<List<CarritoItem>> callback) {

        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + "/carrito")
                .get()
        ).build();

        executor.execute(() -> {

            Response response = null;

            try {
                response = client.newCall(request).execute();

                String json = response.body() != null ? response.body().string() : "";

                Type type = new TypeToken<List<CarritoItem>>() {}.getType();

                List<CarritoItem> list = gson.fromJson(json, type);

                Response finalResponse = response;
                mainHandler.post(() -> {

                    if (finalResponse.isSuccessful()) {
                        callback.onSuccess(list);
                    } else {
                        callback.onError("Error al cargar carrito");
                    }
                });

            } catch (Exception e) {

                mainHandler.post(() -> callback.onError("Error de conexión: " + e.getMessage()));

            } finally {
                if (response != null) response.close();
            }
        });
    }

    public void postRaw(String endpoint, String json, ApiCallback<Map<String, Object>> callback) {

        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(RequestBody.create(json, MediaType.parse("application/json")))
        ).build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }

    public void putRaw(String endpoint, String json, ApiCallback<Map<String, Object>> callback) {

        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + endpoint)
                .put(RequestBody.create(json, MediaType.parse("application/json")))
        ).build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }

    public void delete(String endpoint, ApiCallback<Map<String, Object>> callback) {

        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + endpoint)
                .delete()
        ).build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }

    private <T> void executeRequest(Request request, Type type, ApiCallback<T> callback) {

        executor.execute(() -> {

            Response response = null;

            try {
                response = client.newCall(request).execute();

                String body = response.body() != null ? response.body().string() : "";

                Response finalResponse = response;
                mainHandler.post(() -> {

                    if (finalResponse.isSuccessful()) {

                        T result = gson.fromJson(body, type);
                        callback.onSuccess(result);

                    } else {
                        callback.onError("HTTP " + finalResponse.code() + " -> " + body);
                    }
                });

            } catch (IOException e) {

                mainHandler.post(() -> callback.onError("Error de conexión: " + e.getMessage()));

            } finally {
                if (response != null) response.close();
            }
        });
    }

    public void getProductosPorCategoria(Long id, ApiCallback<List<Producto>> callback) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/productos/categoria/" + id)
                .get()
                .build();

        executeRequest(request, new com.google.gson.reflect.TypeToken<List<Producto>>() {}.getType(), callback);
    }

    public void getTodosLosProductos(ApiCallback<List<Producto>> callback) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/productos")
                .get()
                .build();

        executeRequest(request, new TypeToken<List<Producto>>() {}.getType(), callback);
    }

    public void getPedidosPorUsuario(Long usuarioId, ApiCallback<List<Map<String, Object>>> callback) {

        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + "/pedidos/usuario/" + usuarioId)
                .get()
        ).build();

        executeRequest(request, new TypeToken<List<Map<String, Object>>>(){}.getType(), callback);
    }

    public void crearPedido(CrearPedidoRequest request, ApiCallback<Map<String, Object>> callback) {
        String json = gson.toJson(request);

        Request.Builder builder = addAuthHeader(new Request.Builder()
                .url(BASE_URL + "/pedidos")
                .post(RequestBody.create(json, MediaType.parse("application/json"))));

        executeRequest(builder.build(), new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }

    public void createPayPalOrder(Long pedidoId, ApiCallback<Map<String, Object>> callback) {
        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + "/payments/create-order?pedidoId=" + pedidoId)
                .post(RequestBody.create("", MediaType.parse("application/json")))
        ).build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }

    public void capturePayPalOrder(String orderId, ApiCallback<Map<String, Object>> callback) {

        Request request = addAuthHeader(new Request.Builder()
                .url(BASE_URL + "/payments/capture/" + orderId)
                .post(RequestBody.create("", MediaType.parse("application/json")))
        ).build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }
    public void createPayPalOrderDirect(double amount, String currency, String description, ApiCallback<Map<String, Object>> callback) {
        String json = new com.google.gson.Gson().toJson(Map.of(
                "amount", amount,
                "currency", currency,
                "description", description
        ));

        Request request = addAuthHeader(
                new Request.Builder()
                        .url(BASE_URL + "/payments/create-order-direct")
                        .post(RequestBody.create(json, MediaType.parse("application/json")))
        ).build();

        executeRequest(request, new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }
    public void getUsuarios(ApiCallback<List<Map<String, Object>>> callback) {
        Request request = addAuthHeader(
                new Request.Builder()
                        .url(BASE_URL + "/admin/usuarios")
                        .get()
        ).build();
        executeRequest(request,
                new TypeToken<List<Map<String, Object>>>(){}.getType(), callback);
    }
    public void crearUsuarioAdmin(String username, String password,
                                  String email, String rol,
                                  ApiCallback<Map<String, Object>> callback) {
        Map<String, String> body = new java.util.HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        body.put("rol", rol);

        Request request = addAuthHeader(
                new Request.Builder()
                        .url(BASE_URL + "/admin/usuarios")
                        .post(RequestBody.create(
                                gson.toJson(body),
                                MediaType.parse("application/json")))
        ).build();
        executeRequest(request,
                new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }
    public void eliminarUsuario(Long id, ApiCallback<Map<String, Object>> callback) {
        Request request = addAuthHeader(
                new Request.Builder()
                        .url(BASE_URL + "/admin/usuarios/" + id)
                        .delete()
        ).build();
        executeRequest(request,
                new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }
    public void cambiarRolUsuario(Long id, String nuevoRol,
                                  ApiCallback<Map<String, Object>> callback) {
        Map<String, String> body = new java.util.HashMap<>();
        body.put("rol", nuevoRol);

        Request request = addAuthHeader(
                new Request.Builder()
                        .url(BASE_URL + "/admin/usuarios/" + id + "/rol")
                        .put(RequestBody.create(
                                gson.toJson(body),
                                MediaType.parse("application/json")))
        ).build();
        executeRequest(request,
                new TypeToken<Map<String, Object>>(){}.getType(), callback);
    }
}