package com.example.heleneapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class CheckoutActivity extends AppCompatActivity {

    private String carritoJson;
    private String username;

    private TextInputEditText inputNombre, inputCalle, inputNumero, inputPiso, inputCiudad, inputCodigoPostal, inputProvincia, inputPais, inputTelefono, inputInstrucciones;
    private CheckBox chkGuardarDireccion;
    private Button btnContinuar, btnCancelar;

    private double subtotal, costeEnvio, totalPagar, descuentoAplicado;

    private String getPrefKey() {
        Long userId = UserSession.getInstance().getUserId();
        return "direccion_envio_" + userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_envio);

        carritoJson = getIntent().getStringExtra("carrito_json");

        subtotal = getIntent().getDoubleExtra("subtotal", 0);
        costeEnvio = getIntent().getDoubleExtra("costeEnvio", 4.95);
        totalPagar = getIntent().getDoubleExtra("totalPagar", 0);
        descuentoAplicado = getIntent().getDoubleExtra("descuentoAplicado", 0);


        username = UserSession.getInstance().getUsername();
        if (username == null || username.isEmpty()) {
            username = "Usuario";
        }

        inputNombre = findViewById(R.id.inputNombreCompleto);
        inputCalle = findViewById(R.id.inputCalle);
        inputNumero = findViewById(R.id.inputNumero);
        inputPiso = findViewById(R.id.inputPiso);
        inputCiudad = findViewById(R.id.inputCiudad);
        inputCodigoPostal = findViewById(R.id.inputCodigoPostal);
        inputProvincia = findViewById(R.id.inputProvincia);
        inputPais = findViewById(R.id.inputPais);
        inputTelefono = findViewById(R.id.inputTelefono);
        inputInstrucciones = findViewById(R.id.inputInstrucciones);
        chkGuardarDireccion = findViewById(R.id.chkGuardarDireccion);
        btnContinuar = findViewById(R.id.btnContinuarPago);
        btnCancelar = findViewById(R.id.btnCancelarDireccion);

        cargarDireccionGuardada();

        btnCancelar.setOnClickListener(v -> finish());

        btnContinuar.setOnClickListener(v -> {
            if (validarCampos()) {
                guardarDireccionSiCorresponde();
                irMetodoPago();
            }
        });
    }

    private boolean validarCampos() {
        if (inputNombre.getText().toString().trim().isEmpty()) return error(inputNombre, "Nombre requerido");
        if (inputCalle.getText().toString().trim().isEmpty()) return error(inputCalle, "Calle requerida");
        if (inputNumero.getText().toString().trim().isEmpty()) return error(inputNumero, "Número requerido");
        if (inputCiudad.getText().toString().trim().isEmpty()) return error(inputCiudad, "Ciudad requerida");
        if (inputCodigoPostal.getText().toString().trim().isEmpty()) return error(inputCodigoPostal, "CP requerido");
        if (inputProvincia.getText().toString().trim().isEmpty()) return error(inputProvincia, "Provincia requerida");
        if (inputPais.getText().toString().trim().isEmpty()) return error(inputPais, "País requerido");
        if (inputTelefono.getText().toString().trim().isEmpty()) return error(inputTelefono, "Teléfono requerido");
        return true;
    }

    private boolean error(TextInputEditText field, String msg) {
        field.setError(msg);
        return false;
    }

    private void guardarDireccionSiCorresponde() {
        if (!chkGuardarDireccion.isChecked()) return;

        DireccionEnvio d = new DireccionEnvio();
        d.setNombreCompleto(inputNombre.getText().toString().trim());
        d.setCalle(inputCalle.getText().toString().trim());
        d.setNumero(inputNumero.getText().toString().trim());
        d.setPiso(inputPiso.getText().toString().trim());
        d.setCiudad(inputCiudad.getText().toString().trim());
        d.setCodigoPostal(inputCodigoPostal.getText().toString().trim());
        d.setProvincia(inputProvincia.getText().toString().trim());
        d.setPais(inputPais.getText().toString().trim());
        d.setTelefono(inputTelefono.getText().toString().trim());
        d.setInstruccionesEspeciales(inputInstrucciones.getText().toString().trim());

        getSharedPreferences("HelenePrefs", MODE_PRIVATE)
                .edit()
                .putString(getPrefKey(), new Gson().toJson(d))
                .apply();
    }

    private void cargarDireccionGuardada() {

        String json = getSharedPreferences("HelenePrefs", MODE_PRIVATE).getString(getPrefKey(), null);

        if (json == null) return;

        DireccionEnvio d = new Gson().fromJson(json, DireccionEnvio.class);

        inputNombre.setText(d.getNombreCompleto());
        inputCalle.setText(d.getCalle());
        inputNumero.setText(d.getNumero());
        inputPiso.setText(d.getPiso());
        inputCiudad.setText(d.getCiudad());
        inputCodigoPostal.setText(d.getCodigoPostal());
        inputProvincia.setText(d.getProvincia());
        inputPais.setText(d.getPais());
        inputTelefono.setText(d.getTelefono());
    }

    private void irMetodoPago() {

        Intent intent = new Intent(this, MetodoPagoActivity.class);

        intent.putExtra("subtotal", subtotal);
        intent.putExtra("costeEnvio", costeEnvio);
        intent.putExtra("totalPagar", totalPagar);
        intent.putExtra("descuentoAplicado", descuentoAplicado);
        intent.putExtra("carrito_json", carritoJson);
        intent.putExtra("nombreCompleto", inputNombre.getText().toString().trim());
        intent.putExtra("calle", inputCalle.getText().toString().trim());
        intent.putExtra("numero", inputNumero.getText().toString().trim());
        intent.putExtra("piso", inputPiso.getText().toString().trim());
        intent.putExtra("ciudad", inputCiudad.getText().toString().trim());
        intent.putExtra("codigoPostal", inputCodigoPostal.getText().toString().trim());
        intent.putExtra("provincia", inputProvincia.getText().toString().trim());
        intent.putExtra("pais", inputPais.getText().toString().trim());
        intent.putExtra("telefono", inputTelefono.getText().toString().trim());
        intent.putExtra("instrucciones", inputInstrucciones.getText().toString().trim());

        startActivity(intent);
    }

}