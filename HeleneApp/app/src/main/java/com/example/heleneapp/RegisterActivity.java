package com.example.heleneapp;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heleneapp.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText usernameInput, emailInput,passwordInput, confirmPasswordInput;
    private Button registerSubmitButton;
    private Button backButton;

    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiClient = new ApiClient();

        usernameInput = findViewById(R.id.usernameInput);
        emailInput           = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        registerSubmitButton = findViewById(R.id.registerSubmitButton);
        backButton = findViewById(R.id.backButton);

        registerSubmitButton.setOnClickListener(v -> register());
        backButton.setOnClickListener(v -> finish());
    }

    private void register() {

        String username = usernameInput.getText() != null
                ? usernameInput.getText().toString().trim()
                : "";

        String email = emailInput.getText() != null
                ? emailInput.getText().toString().trim()
                : "";

        String password = passwordInput.getText() != null
                ? passwordInput.getText().toString().trim()
                : "";

        String confirm = confirmPasswordInput.getText() != null
                ? confirmPasswordInput.getText().toString().trim()
                : "";

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        registerSubmitButton.setEnabled(false);

        apiClient.register(username, email, password, new ApiClient.ApiCallback<Map<String, Object>>() {

            @Override
            public void onSuccess(Map<String, Object> response) {

                registerSubmitButton.setEnabled(true);

                Object successObj = response.get("success");
                boolean success = successObj != null
                        && Boolean.parseBoolean(successObj.toString());

                if (success) {
                    Toast.makeText(RegisterActivity.this, "¡ Bienvenid@ !", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    String message = response.get("message") != null ? response.get("message").toString() : "No se pudo registrar";

                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                registerSubmitButton.setEnabled(true);
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}