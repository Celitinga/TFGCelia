package com.example.heleneapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heleneapp.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetodoPagoActivity extends AppCompatActivity {

    private String username;
    private ApiClient apiClient;
    private String metodoPagoSeleccionado = "";

    private double subtotal, costeEnvio, totalPagar, descuentoAplicado;
    private DireccionEnvio direccionEnvio;

    private Button btnConfirmar, btnCancelar;
    private RadioButton radioPaypal, radioTarjeta, radioEfectivo;
    private LinearLayout optionPaypal, optionTarjeta, optionEfectivo;
    private TextInputEditText inputNumero, inputFecha, inputCvv;
    private TextView txtSubtotal, txtEnvio, txtTotal;

    private static final String PREFS = "paypal_prefs";
    private static final String KEY_ORDER = "pending_order_id";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_pago);

        subtotal = getIntent().getDoubleExtra("subtotal", 0);
        costeEnvio = getIntent().getDoubleExtra("costeEnvio", 0);
        totalPagar = getIntent().getDoubleExtra("totalPagar", 0);
        descuentoAplicado = getIntent().getDoubleExtra("descuentoAplicado", 0);

        direccionEnvio = new DireccionEnvio();
        direccionEnvio.setNombreCompleto(getIntent().getStringExtra("nombreCompleto"));
        direccionEnvio.setCalle(getIntent().getStringExtra("calle"));
        direccionEnvio.setNumero(getIntent().getStringExtra("numero"));
        direccionEnvio.setPiso(getIntent().getStringExtra("piso"));
        direccionEnvio.setCiudad(getIntent().getStringExtra("ciudad"));
        direccionEnvio.setCodigoPostal(getIntent().getStringExtra("codigoPostal"));
        direccionEnvio.setProvincia(getIntent().getStringExtra("provincia"));
        direccionEnvio.setPais(getIntent().getStringExtra("pais"));
        direccionEnvio.setTelefono(getIntent().getStringExtra("telefono"));
        direccionEnvio.setInstruccionesEspeciales(getIntent().getStringExtra("instrucciones"));

        username = UserSession.getInstance().getUsername();
        if (username == null || username.isEmpty()) username = "Usuario";

        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtEnvio    = findViewById(R.id.txtEnvio);
        txtTotal    = findViewById(R.id.txtTotal);

        txtSubtotal.setText(String.format("%.2f €", subtotal));
        txtEnvio.setText(costeEnvio == 0 ? "GRATIS" : String.format("%.2f €", costeEnvio));
        txtTotal.setText(String.format("%.2f €", totalPagar));

        btnConfirmar   = findViewById(R.id.btnConfirmarPago);
        btnCancelar    = findViewById(R.id.btnCancelarPago);
        optionPaypal   = findViewById(R.id.optionPaypal);
        optionTarjeta  = findViewById(R.id.optionTarjeta);
        optionEfectivo = findViewById(R.id.optionEfectivo);
        radioPaypal    = findViewById(R.id.radioPaypal);
        radioTarjeta   = findViewById(R.id.radioTarjeta);
        radioEfectivo  = findViewById(R.id.radioEfectivo);

        limpiarRadios();

        optionPaypal.setOnClickListener(v -> {
            seleccionarRadio(radioPaypal);
            metodoPagoSeleccionado = "PAYPAL";
        });
        optionTarjeta.setOnClickListener(v -> {
            seleccionarRadio(radioTarjeta);
            metodoPagoSeleccionado = "TARJETA_CREDITO";
        });
        optionEfectivo.setOnClickListener(v -> {
            seleccionarRadio(radioEfectivo);
            metodoPagoSeleccionado = "CONTRAENTREGA";
        });

        btnCancelar.setOnClickListener(v -> finish());
        btnConfirmar.setOnClickListener(v -> {
            if (metodoPagoSeleccionado.isEmpty()) {
                Toast.makeText(this, "Selecciona un método de pago", Toast.LENGTH_SHORT).show();
                return;
            }
            procesarPago();
        });

        apiClient = new ApiClient();
    }

    private void limpiarRadios() {
        radioPaypal.setChecked(false);
        radioTarjeta.setChecked(false);
        radioEfectivo.setChecked(false);
    }

    private void seleccionarRadio(RadioButton seleccionado) {
        limpiarRadios();
        seleccionado.setChecked(true);
    }

    private void procesarPago() {
        switch (metodoPagoSeleccionado) {
            case "PAYPAL":
                iniciarFlujoPayPal();
                break;
            case "TARJETA_CREDITO":
                mostrarDialogoTarjeta();
                break;
            case "CONTRAENTREGA":
                confirmarContraentrega();
                break;
        }
    }

    private void iniciarFlujoPayPal() {
        btnConfirmar.setEnabled(false);
        Toast.makeText(this, "Conectando con PayPal...", Toast.LENGTH_SHORT).show();

        apiClient.createPayPalOrderDirect(totalPagar, "EUR", "Compra Helene Cosmética Natural", new ApiClient.ApiCallback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> response) {
                        String orderId    = (String) response.get("orderId");
                        String approveUrl = (String) response.get("approveUrl");

                        if (orderId == null || approveUrl == null) {
                            Toast.makeText(MetodoPagoActivity.this, "Error: respuesta incompleta de PayPal", Toast.LENGTH_LONG).show();
                            btnConfirmar.setEnabled(true);
                            return;
                        }
                        guardarPendingOrder(orderId);

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(approveUrl)));
                        btnConfirmar.setEnabled(true);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(MetodoPagoActivity.this, "Error al conectar con PayPal: " + error, Toast.LENGTH_LONG).show();
                        btnConfirmar.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Uri data = intent.getData();
        if (data == null) return;

        if ("paypalpay".equals(data.getHost())) {
            crearPedidoYCapturar();
        } else if ("paypalcancel".equals(data.getHost())) {
            clearPendingOrder();
            Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
        }
    }
    private void crearPedidoYCapturar() {
        String orderId = getPendingOrderId();
        if (orderId == null) {
            Toast.makeText(this, "Error: no hay pago pendiente", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Confirmando pago...", Toast.LENGTH_SHORT).show();

        CrearPedidoRequest request = construirPedidoRequest();
        request.setPaypalPaymentId(orderId);

        apiClient.crearPedido(request, new ApiClient.ApiCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                Object idObj = response.get("pedidoId");
                if (idObj == null) {
                    Toast.makeText(MetodoPagoActivity.this, "Error: pedido creado pero sin ID", Toast.LENGTH_LONG).show();
                    return;
                }
                Long pedidoId = ((Double) idObj).longValue();
                capturarPago(orderId, pedidoId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MetodoPagoActivity.this, "Error al crear pedido: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void capturarPago(String orderId, Long pedidoId) {
        apiClient.capturePayPalOrder(orderId, new ApiClient.ApiCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                clearPendingOrder();
                String numeroPedido = (String) response.get("numeroPedido");
                NotificationHelper.showNotification(MetodoPagoActivity.this, "Se ha realizado una compra", "Se ha completado la compra del pedido" + numeroPedido );
                Toast.makeText(MetodoPagoActivity.this, "¡Pago completado! Pedido #" + numeroPedido, Toast.LENGTH_LONG).show();
                irAConfirmacion();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MetodoPagoActivity.this, "Error al confirmar pago: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void mostrarDialogoTarjeta() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_tarjeta_credito, null);
        builder.setView(view);
        android.app.AlertDialog dialog = builder.create();

        inputNumero = view.findViewById(R.id.inputNumeroTarjeta);
        inputFecha = view.findViewById(R.id.inputFechaVencimiento);
        inputCvv = view.findViewById(R.id.inputCvv);

        view.findViewById(R.id.btnPagarTarjeta).setOnClickListener(v -> {
            if (inputNumero.getText().toString().trim().isEmpty() || inputFecha.getText().toString().trim().isEmpty() || inputCvv.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Completa todos los datos", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            enviarPedidoAlBackend(null);
        });

        view.findViewById(R.id.btnCancelarTarjeta).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void confirmarContraentrega() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmar pedido")
                .setMessage(String.format(
                        "Total a pagar contraentrega: %.2f €\n¿Confirmas el pedido?", totalPagar))
                .setPositiveButton("Confirmar", (dialog, which) -> enviarPedidoAlBackend(null))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarPedidoAlBackend(String paypalPaymentId) {
        CrearPedidoRequest request = construirPedidoRequest();
        request.setPaypalPaymentId(paypalPaymentId);

        apiClient.crearPedido(request, new ApiClient.ApiCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                NotificationHelper.showNotification(MetodoPagoActivity.this, "Compra realizada", "Pedido creado #" + response.get("numeroPedido"));
                Toast.makeText(MetodoPagoActivity.this, "Pedido creado #" + response.get("numeroPedido"), Toast.LENGTH_LONG).show();
                irAConfirmacion();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MetodoPagoActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private CrearPedidoRequest construirPedidoRequest() {
        String carritoJson = getIntent().getStringExtra("carrito_json");
        List<CarritoItem> carrito;
        List<ItemPedidoRequest> items = new ArrayList<>();

        try {
            carrito = carritoJson == null || carritoJson.isEmpty()
                    ? new ArrayList<>()
                    : new com.google.gson.Gson().fromJson(carritoJson,
                    new com.google.gson.reflect.TypeToken<List<CarritoItem>>(){}.getType());

            for (CarritoItem c : carrito) {
                ItemPedidoRequest item = new ItemPedidoRequest();
                item.setProductoId(c.getProductoId());
                item.setCantidad(c.getCantidad());
                item.setPrecioUnitario(c.getPrecioProducto());
                item.setDescuento(c.getDescuento());
                items.add(item);
            }
        } catch (Exception e) {
            items = new ArrayList<>();
        }

        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setUsuarioId(UserSession.getInstance().getUserId());
        request.setDireccionEnvio(direccionEnvio);
        request.setItems(items);
        request.setSubtotal(subtotal);
        request.setCosteEnvio(costeEnvio);
        request.setTotal(totalPagar);
        request.setDescuentoAplicado(descuentoAplicado);
        request.setMetodoPago(metodoPagoSeleccionado);
        return request;
    }

    private void irAConfirmacion() {
        Intent intent = new Intent(this, CarritoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void guardarPendingOrder(String orderId) {
        getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_ORDER, orderId).apply();
    }

    private String getPendingOrderId() {
        return getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_ORDER, null);
    }

    private void clearPendingOrder() {
        getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY_ORDER).apply();
    }
}