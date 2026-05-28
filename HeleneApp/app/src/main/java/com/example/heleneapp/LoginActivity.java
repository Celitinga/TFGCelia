package com.example.heleneapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heleneapp.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private Button registerButton;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiClient = new ApiClient();

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> login());
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void login() {
        String user = usernameInput.getText() != null ? usernameInput.getText().toString().trim() : "";

        String pass = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);

        apiClient.login(user, pass, new ApiClient.ApiCallback<Map<String, Object>>() {

            @Override
            public void onSuccess(Map<String, Object> response) {
                loginButton.setEnabled(true);

                boolean success = Boolean.parseBoolean(String.valueOf(response.get("success")));

                if (!success) {
                    Toast.makeText(LoginActivity.this, String.valueOf(response.get("message")), Toast.LENGTH_SHORT).show();
                    return;
                }

                String username = String.valueOf(response.get("username"));

                String token = String.valueOf(response.get("token"));

                Object rawId = response.get("userId");
                long userId;

                if (rawId instanceof Number) {
                    userId = ((Number) rawId).longValue();
                } else {
                    userId = (long) Double.parseDouble(rawId.toString());
                }

                List<String> rolesList = extractRoles(response.get("roles"));

                UserSession.getInstance().setUser(userId, username, token);
                UserSession.getInstance().setRoles(rolesList);

                SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
                prefs.edit()
                        .putLong("userId", userId)
                        .putString("username", username)
                        .putString("token", token)
                        .putStringSet("roles", new HashSet<>(rolesList))
                        .apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            private List<String> extractRoles(Object rolesObj) {

                List<String> rolesList = new ArrayList<>();

                if (rolesObj instanceof List<?> list) {
                    for (Object r : list) {
                        rolesList.add(String.valueOf(r));
                    }
                }

                return rolesList;
            }

            @Override
            public void onError(String error) {
                loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}