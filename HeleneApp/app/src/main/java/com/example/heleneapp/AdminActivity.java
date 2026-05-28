package com.example.heleneapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heleneapp.network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private ApiClient apiClient;
    private ListView listViewUsuarios;
    private final List<Map<String, Object>> listaUsuarios = new ArrayList<>();
    private UsuarioAdminAdapter adapter;
    private TextInputEditText inputUsername, inputPassword, inputEmail;
    private Spinner spinnerRol;
    private ImageButton btnVolver;
    private Button btnNuevoUsuario;
    private ArrayAdapter<String> rolesAdapter;
    private MaterialButton btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        apiClient = new ApiClient();

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        btnNuevoUsuario = findViewById(R.id.btnNuevoUsuario);
        btnNuevoUsuario.setOnClickListener(v -> mostrarDialogoCrear());

        listViewUsuarios = findViewById(R.id.listViewUsuarios);
        adapter = new UsuarioAdminAdapter(this, listaUsuarios, this::mostrarOpcionesUsuario);
        listViewUsuarios.setAdapter(adapter);

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        apiClient.getUsuarios(new ApiClient.ApiCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> response) {
                listaUsuarios.clear();
                listaUsuarios.addAll(response);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminActivity.this, "Error al cargar usuarios: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoCrear() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_crear_usuario, null);

        inputUsername = view.findViewById(R.id.inputUsername);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputEmail    = view.findViewById(R.id.inputEmail);
        spinnerRol    = view.findViewById(R.id.spinnerRol);

        rolesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"CLIENTE", "EMPLEADO", "SUSCRIPTOR", "ADMIN"});
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolesAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {

            String nuevoUsername = inputUsername.getText().toString().trim();
            String nuevoPassword = inputPassword.getText().toString().trim();
            String nuevoEmail = inputEmail.getText().toString().trim();
            String nuevoRol = spinnerRol.getSelectedItem().toString();

            if (nuevoUsername.isEmpty()) {
                inputUsername.setError("Obligatorio");
                return;
            }
            if (nuevoPassword.isEmpty() || nuevoPassword.length() < 6) {
                inputPassword.setError("Mínimo 6 caracteres");
                return;
            }

            btnGuardar.setEnabled(false);

            apiClient.crearUsuarioAdmin(nuevoUsername, nuevoPassword, nuevoEmail, nuevoRol, new ApiClient.ApiCallback<Map<String, Object>>() {

                        @Override
                        public void onSuccess(Map<String, Object> response) {
                            dialog.dismiss();
                            Toast.makeText(AdminActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                            cargarUsuarios();
                        }

                        @Override
                        public void onError(String error) {
                            btnGuardar.setEnabled(true);
                            Toast.makeText(AdminActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        dialog.show();
    }
    private void mostrarOpcionesUsuario(Map<String, Object> usuario) {
        Long id = ((Double) usuario.get("id")).longValue();

        String nombreUsuario = usuario.get("username") != null ? (String) usuario.get("username") : "";

        boolean esAdmin = false;
        try {
            List<?> roles = (List<?>) usuario.get("roles");
            if (roles != null) {
                for (Object rolObj : roles) {
                    Map<?, ?> rol = (Map<?, ?>) rolObj;
                    if ("ADMIN".equals(rol.get("nombre"))) {
                        esAdmin = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            esAdmin = false;
        }

        String[] opciones = esAdmin ? new String[]{"Cambiar rol"} : new String[]{"Cambiar rol", "Eliminar"};

        boolean finalEsAdmin = esAdmin;

        new AlertDialog.Builder(this)
                .setTitle("Usuario: " + nombreUsuario)
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoCambiarRol(id, nombreUsuario);
                    } else if (!finalEsAdmin) {
                        confirmarEliminar(id, nombreUsuario);
                    }
                })
                .show();
    }
    private void mostrarDialogoCambiarRol(Long id, String nombreUsuario) {
        String[] roles = {"CLIENTE", "EMPLEADO", "SUSCRIPTOR", "ADMIN"};

        new AlertDialog.Builder(this)
                .setTitle("Selecciona rol")
                .setItems(roles, (dialog, which) ->
                        apiClient.cambiarRolUsuario(id, roles[which],
                                new ApiClient.ApiCallback<Map<String, Object>>() {
                                    @Override
                                    public void onSuccess(Map<String, Object> response) {
                                        Toast.makeText(AdminActivity.this, "Rol actualizado", Toast.LENGTH_SHORT).show();

                                        String miUsername = UserSession.getInstance().getUsername();
                                        if (miUsername != null && miUsername.equals(nombreUsuario)) {
                                            List<String> nuevosRoles = new ArrayList<>();
                                            nuevosRoles.add(roles[which]);
                                            UserSession.getInstance().setRoles(nuevosRoles);
                                        }

                                        cargarUsuarios();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(AdminActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                    }
                                }))
                .show();
    }
    private void confirmarEliminar(Long id, String nombre) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Eliminar a " + nombre + "?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        apiClient.eliminarUsuario(id,
                                new ApiClient.ApiCallback<Map<String, Object>>() {
                                    @Override
                                    public void onSuccess(Map<String, Object> response) {
                                        Toast.makeText(AdminActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                        cargarUsuarios();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(AdminActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                                    }
                                }))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}