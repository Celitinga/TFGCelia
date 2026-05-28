package com.example.heleneapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView gif = findViewById(R.id.gifImage);

        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .into(gif);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);

            long userId = prefs.getLong("userId", -1);
            String username = prefs.getString("username", null);
            String token = prefs.getString("token", null);
            Set<String> rolesSet = prefs.getStringSet("roles", new HashSet<>());
            ArrayList<String> roles = new ArrayList<>(rolesSet);

            if (userId != -1 && token != null) {
                UserSession.getInstance().setUser(userId, username, token);
                UserSession.getInstance().setRoles(roles);
                startActivity(new Intent(this, MainActivity.class));

            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }

            finish();

        }, 2000);
    }
}