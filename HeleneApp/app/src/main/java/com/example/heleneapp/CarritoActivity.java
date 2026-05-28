package com.example.heleneapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heleneapp.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private View menuHome, menuProfile,menuAdmin, menuInstagram, menuLogout, menuOrders;
    private TextView menuUsernameText, totalText, emptyCartText;
    private RecyclerView recyclerCarrito;
    private ApiClient apiClient;
    private CarritoAdapter adapter;
    private List<CarritoItem> lista = new ArrayList<>();
    private String username;
    private Button btnComprar;

    private double subtotal = 0;
    private double costeEnvio = 4.95;
    private double totalPagar = 0;
    private ArrayList<String> roles;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        UserSession session = UserSession.getInstance();
        String token = session.getToken();
        username = session.getUsername();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (username == null || username.isEmpty()) {
            username = "Usuario";
        }

        roles = new ArrayList<>(UserSession.getInstance().getRoles());
        if (roles == null) roles = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        menuHome = findViewById(R.id.menuHome);
        menuProfile = findViewById(R.id.menuProfile);
        menuOrders = findViewById(R.id.menuOrders);
        menuAdmin = findViewById(R.id.menuAdmin);
        menuLogout = findViewById(R.id.menuLogout);
        menuInstagram = findViewById(R.id.menuInstagram);
        menuUsernameText = findViewById(R.id.menuUsernameText);

        menuUsernameText.setText(username);

        if (roles.contains("ADMIN")) {
            menuAdmin.setVisibility(View.VISIBLE);
        } else {
            menuAdmin.setVisibility(View.GONE);
        }

        menuButton.setOnClickListener(v -> toggleDrawer());
        setupMenuListeners();

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        totalText = findViewById(R.id.totalText);
        emptyCartText = findViewById(R.id.emptyCartText);
        btnComprar = findViewById(R.id.btnComprar);

        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));
        recyclerCarrito.setHasFixedSize(true);

        apiClient = new ApiClient();

        adapter = new CarritoAdapter(lista, this, apiClient, this);
        recyclerCarrito.setAdapter(adapter);

        btnComprar.setOnClickListener(v -> {

            if (lista.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CarritoActivity.this, CheckoutActivity.class);

            double subtotal = 0;
            double descuentoTotal = 0;

            for (CarritoItem item : lista) {

                double subtotalItem = item.getPrecioProducto() * item.getCantidad();

                subtotal += subtotalItem;

                descuentoTotal += subtotalItem * (item.getDescuento() / 100.0);
            }

            double envio = subtotal > 50 ? 0 : 4.95;
            double total = subtotal - descuentoTotal + envio;

            intent.putExtra("subtotal", subtotal);
            intent.putExtra("costeEnvio", envio);
            intent.putExtra("totalPagar", total);
            intent.putExtra("descuentoAplicado", descuentoTotal);

            String carritoJson = new com.google.gson.Gson().toJson(lista);
            intent.putExtra("carrito_json", carritoJson);

            startActivity(intent);
        });

        cargarCarrito();
    }

    private void cargarCarrito() {
        apiClient.getCarrito(new ApiClient.ApiCallback<List<CarritoItem>>() {
            @Override
            public void onSuccess(List<CarritoItem> response) {
                lista.clear();
                lista.addAll(response);
                adapter.notifyDataSetChanged();
                actualizarUI();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(CarritoActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void actualizarUI() {
        if (lista.isEmpty()) {
            emptyCartText.setVisibility(View.VISIBLE);
            totalText.setText("Total: 0 €");
            btnComprar.setEnabled(false);
        } else {
            emptyCartText.setVisibility(View.GONE);
            calcularTotal();
            btnComprar.setEnabled(true);
        }
    }

    public void calcularTotal() {
        subtotal = 0;
        for (CarritoItem item : lista) {
            subtotal += item.getSubtotal();
        }

        if (subtotal > 50) {
            costeEnvio = 0;
        } else {
            costeEnvio = 4.95;
        }

        totalPagar = subtotal + costeEnvio;
        totalText.setText(String.format("Total: %.2f €", totalPagar));
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarCarrito();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
    private void setupMenuListeners() {
        menuHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });;

        menuProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        menuOrders.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, HistorialPedidosActivity.class));
        });

        menuAdmin.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, AdminActivity.class));
        });

        menuInstagram.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openInstagram();
        });

        menuLogout.setOnClickListener(v -> {

            UserSession.getInstance().clear();

            SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
    private void openInstagram() {
        String url = "https://www.instagram.com/helene_cosmeticanatural/";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=helene_cosmeticanatural"));
            if (intent.resolveActivity(getPackageManager()) == null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            }
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }
}