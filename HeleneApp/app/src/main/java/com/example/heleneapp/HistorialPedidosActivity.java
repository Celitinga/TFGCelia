package com.example.heleneapp;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heleneapp.network.ApiClient;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorialPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ApiClient apiClient;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lang = LocaleHelper.getSavedLocale(this);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        config.setLocales(new android.os.LocaleList(locale));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_pedidos);

        recyclerView = findViewById(R.id.recyclerPedidos);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiClient = new ApiClient();

        cargarPedidos();
    }

    private void cargarPedidos() {

        progressBar.setVisibility(View.VISIBLE);

        Long userId = UserSession.getInstance().getUserId();

        apiClient.getPedidosPorUsuario(userId, new ApiClient.ApiCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> response) {

                progressBar.setVisibility(View.GONE);

                recyclerView.setAdapter(new PedidoAdapter(response));
            }

            @Override
            public void onError(String error) {

                progressBar.setVisibility(View.GONE);

                Toast.makeText(HistorialPedidosActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}