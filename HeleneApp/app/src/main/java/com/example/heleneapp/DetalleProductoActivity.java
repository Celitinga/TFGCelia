package com.example.heleneapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.heleneapp.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class DetalleProductoActivity extends AppCompatActivity {

    private ImageView image;
    private TextView nombre, precio, descripcion;
    private Button btnEditar, btnVolver, btnComprar;
    private ApiClient apiClient;
    private Long usuarioId, productoId, categoriaId;
    private String imagenActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        usuarioId = UserSession.getInstance().getUserId();

        if (usuarioId == null || usuarioId == -1) {
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        image = findViewById(R.id.detailImage);
        nombre = findViewById(R.id.detailNombre);
        precio = findViewById(R.id.detailPrecio);
        descripcion = findViewById(R.id.detailDescripcion);
        btnEditar = findViewById(R.id.btnEditar);
        btnComprar = findViewById(R.id.btnComprar);
        btnVolver = findViewById(R.id.btnVolver);

        apiClient = new ApiClient();

        String nombreProd = getIntent().getStringExtra("nombre");
        String descripcionProd = getIntent().getStringExtra("descripcion");
        double precioProd = getIntent().getDoubleExtra("precio", 0);
        imagenActual = getIntent().getStringExtra("imagen");
        productoId = getIntent().getLongExtra("productoId", -1);
        categoriaId = getIntent().getLongExtra("categoriaId", -1);

        nombre.setText(nombreProd);
        descripcion.setText(descripcionProd);
        precio.setText(String.format("%.2f €", precioProd));
        Glide.with(this).load(imagenActual).into(image);

        btnVolver.setOnClickListener(v -> finish());
        btnComprar.setOnClickListener(v -> agregarAlCarrito());

        List<String> roles = UserSession.getInstance().getRoles();
        boolean puedeEditar = roles != null && (roles.contains("ADMIN") || roles.contains("EMPLEADO"));
        btnEditar.setVisibility(puedeEditar ? View.VISIBLE : View.GONE);
        btnEditar.setOnClickListener(v -> mostrarDialogEditar());
    }

    private void agregarAlCarrito() {
        String json = "{"
                + "\"usuarioId\":" + usuarioId + ","
                + "\"productoId\":" + productoId + ","
                + "\"cantidad\":1"
                + "}";

        apiClient.postRaw("/carrito", json, new ApiClient.ApiCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                Toast.makeText(DetalleProductoActivity.this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DetalleProductoActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogEditar() {
        View view = getLayoutInflater().inflate(R.layout.dialog_editar_producto, null);

        TextInputEditText editNombre = view.findViewById(R.id.inputNombre);
        TextInputEditText editPrecio = view.findViewById(R.id.inputPrecio);
        TextInputEditText editDescripcion = view.findViewById(R.id.inputDescripcion);
        TextInputEditText editImagen = view.findViewById(R.id.inputImagen);
        TextInputEditText editDescuento = view.findViewById(R.id.inputDescuento);

        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        editNombre.setText(nombre.getText().toString());

        String raw = precio.getText().toString()
                .replace("€", "")
                .trim()
                .replace(",", ".");

        double precioActual = Double.parseDouble(raw);
        editPrecio.setText(String.format("%.2f", precioActual).replace(',', '.'));
        editDescripcion.setText(descripcion.getText().toString());
        editImagen.setText(imagenActual);
        editDescuento.setText(String.valueOf(getIntent().getIntExtra("descuento", 0)));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            try {
                if (categoriaId == null || categoriaId == -1) {
                    Toast.makeText(this, "Error: Categoría no válida", Toast.LENGTH_SHORT).show();
                    return;
                }

                String nuevoNombre = editNombre.getText().toString().trim();
                String precioStr = editPrecio.getText().toString().trim();
                String nuevaDescripcion = editDescripcion.getText().toString().trim();
                String nuevaImagen = editImagen.getText().toString().trim();
                String descuentoStr = editDescuento.getText().toString().trim();

                if (nuevoNombre.isEmpty()) {
                    Toast.makeText(this, "Nombre requerido", Toast.LENGTH_SHORT).show();
                    return;
                }

                double nuevoPrecio = Double.parseDouble(precioStr);
                int nuevoDescuento = descuentoStr.isEmpty() ? 0 : Integer.parseInt(descuentoStr);

                JSONObject json = new JSONObject();
                json.put("nombre", nuevoNombre);
                json.put("precioOriginal", nuevoPrecio);
                json.put("descuento", nuevoDescuento);
                json.put("descripcion", nuevaDescripcion);
                json.put("imagenUrl", nuevaImagen);
                json.put("categoriaId", categoriaId);

                apiClient.putRaw("/productos/" + productoId, json.toString(),
                        new ApiClient.ApiCallback<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> response) {
                                NotificationHelper.showToast(DetalleProductoActivity.this,"Producto actualizado" );
                                setResult(RESULT_OK);
                                dialog.dismiss();
                                finish();
                            }

                            @Override
                            public void onError(String error) {
                                NotificationHelper.showToast(DetalleProductoActivity.this, "Error: " + error);
                            }
                        });

            } catch (Exception e) {
                NotificationHelper.showToast(this, "Error: " + e.getMessage());
            }
        });

        dialog.show();
    }
}