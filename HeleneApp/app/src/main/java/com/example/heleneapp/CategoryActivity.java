package com.example.heleneapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
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

public class CategoryActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton menuButton, cartButton;
    private ApiClient apiClient;
    private View menuHome, menuProfile, menuInstagram, menuLogout, menuAdmin, menuOrders;
    private TextView menuUsernameText, title, cartBadge;
    private String username;
    private RecyclerView recyclerView;
    private ProductoAdapter adapterProd;
    private List<Producto> lista = new ArrayList<>();
    private List<Producto> listaOriginal = new ArrayList<>();
    private Long categoriaId;
    private ArrayList<String> roles;
    private SearchView searchView;
    private int cartCount = 0;
    private static final int REQUEST_EDITAR_PRODUCTO = 100;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        username = UserSession.getInstance().getUsername();

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
        menuInstagram = findViewById(R.id.menuInstagram);
        menuUsernameText = findViewById(R.id.menuUsernameText);
        menuLogout = findViewById(R.id.menuLogout);

        menuUsernameText.setText(username);

        if (roles.contains("ADMIN")) {
            menuAdmin.setVisibility(View.VISIBLE);
        } else {
            menuAdmin.setVisibility(View.GONE);
        }

        menuButton.setOnClickListener(v -> toggleDrawer());
        setupMenuListeners();

        title = findViewById(R.id.categoryTitle);
        recyclerView = findViewById(R.id.recyclerProductos);

        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();

        cartButton = findViewById(R.id.cartButton);
        cartBadge = findViewById(R.id.cartBadge);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterProd = new ProductoAdapter(lista, this);
        recyclerView.setAdapter(adapterProd);

        apiClient = new ApiClient();

        if (searchView != null) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filtrar(query);
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    filtrar(newText);
                    return true;
                }
            });
        }

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CarritoActivity.class);
            startActivity(intent);
        });

        loadCartCount();

        String categoryName = getIntent().getStringExtra("category");
        String searchText = getIntent().getStringExtra("searchText");

        categoriaId = getIntent().getLongExtra("categoriaId", -1);

        if (categoryName != null) {
            title.setText(categoryName);
        }


        if (searchText != null && !searchText.trim().isEmpty()) {

            title.setText("Resultados: " + searchText);

            cargarTodosLosProductos(searchText);

        } else if (categoriaId != -1) {

            cargarProductos();

        } else {
            Toast.makeText(this, "Categoría no válida", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarProductos() {

        apiClient.getProductosPorCategoria(categoriaId, new ApiClient.ApiCallback<List<Producto>>() {

            @Override
            public void onSuccess(List<Producto> response) {

                lista.clear();
                lista.addAll(response);

                listaOriginal.clear();
                listaOriginal.addAll(response);

                adapterProd.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CategoryActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTodosLosProductos(String textoBusqueda) {

        apiClient.getTodosLosProductos(
                new ApiClient.ApiCallback<List<Producto>>() {

                    @Override
                    public void onSuccess(List<Producto> response) {

                        lista.clear();

                        listaOriginal.clear();

                        for (Producto producto : response) {

                            if (producto.getNombre() != null && producto.getNombre().toLowerCase().contains(textoBusqueda.toLowerCase())) {

                                lista.add(producto);
                            }
                        }

                        listaOriginal.addAll(lista);

                        adapterProd.notifyDataSetChanged();

                        if (lista.isEmpty()) {

                            Toast.makeText(CategoryActivity.this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(CategoryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void filtrar(String texto) {

        lista.clear();

        if (texto == null || texto.trim().isEmpty()) {

            lista.addAll(listaOriginal);

        } else {

            texto = texto.toLowerCase();

            for (Producto producto : listaOriginal) {

                if (producto.getNombre() != null && producto.getNombre().toLowerCase().contains(texto)) {

                    lista.add(producto);
                }
            }
        }

        adapterProd.notifyDataSetChanged();
    }
    private void loadCartCount() {

        Long userId = UserSession.getInstance().getUserId();

        if (userId == null || userId == -1) return;

        apiClient.getCarrito(new ApiClient.ApiCallback<List<CarritoItem>>() {

            @Override
            public void onSuccess(List<CarritoItem> response) {

                cartCount = 0;

                for (CarritoItem item : response) {
                    cartCount += item.getCantidad();
                }

                updateCartBadge();
            }

            @Override
            public void onError(String error) {

                cartCount = 0;
                updateCartBadge();
            }
        });
    }
    private void updateCartBadge() {

        if (cartBadge == null) return;

        if (cartCount > 0) {

            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(cartCount));

        } else {

            cartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDITAR_PRODUCTO && resultCode == RESULT_OK) {
            if (categoriaId != -1) {
                cargarProductos();
            } else {
                String searchText = getIntent().getStringExtra("searchText");
                if (searchText != null && !searchText.trim().isEmpty()) {
                    cargarTodosLosProductos(searchText);
                }
            }

            loadCartCount();

            NotificationHelper.showToast(this, "Productos actualizados");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
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
        });

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