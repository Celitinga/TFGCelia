package com.example.heleneapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.heleneapp.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private TextView profileUsername, profileEmail;
    private Button editProfileButton, backButton;
    private Long userId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UserSession session = UserSession.getInstance();

        userId = session.getUserId();
        String username = session.getUsername();
        String token = session.getToken();

        if (userId == null || token == null || token.isEmpty()) {

            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
            return;
        }

        profileImage = findViewById(R.id.profileImage);
        profileUsername = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileEmail);
        editProfileButton = findViewById(R.id.editProfileButton);
        backButton = findViewById(R.id.backToMainButton);

        profileUsername.setText(username);
        profileEmail.setText(username + "@helene.com");

        cargarImagenPerfil();

        profileImage.setOnClickListener(v -> abrirGaleria());
        editProfileButton.setOnClickListener(v -> mostrarDialogoEditarPerfil());
        backButton.setOnClickListener(v -> finish());
    }

    private void cargarImagenPerfil() {
        File file = new File(getFilesDir(), "profile_" + userId + ".jpg");
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                profileImage.setImageBitmap(bitmap);
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void mostrarDialogoEditarPerfil() {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText inputUsername = view.findViewById(R.id.editUsername);
        TextInputEditText inputEmail = view.findViewById(R.id.editEmail);

        inputUsername.setText(profileUsername.getText().toString());
        inputEmail.setText(profileEmail.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String nuevoUsername = inputUsername.getText().toString().trim();
            String nuevoEmail = inputEmail.getText().toString().trim();
            if (!nuevoUsername.isEmpty()) {
                actualizarPerfil(nuevoUsername, nuevoEmail);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPerfil(String nuevoUsername, String nuevoEmail) {

        UserSession.getInstance().setUser(userId, nuevoUsername, UserSession.getInstance().getToken());

        profileUsername.setText(nuevoUsername);
        profileEmail.setText(nuevoEmail);

        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                guardarImagen(uri);
            }
        }
    }

    private void guardarImagen(Uri uri) {

        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

            File file = new File(getFilesDir(), "profile_" + userId + ".jpg");

            FileOutputStream out = new FileOutputStream(file);

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

            out.flush();
            out.close();

            profileImage.setImageBitmap(scaledBitmap);

            Toast.makeText(this, "Foto de perfil guardada", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
        }
    }
}