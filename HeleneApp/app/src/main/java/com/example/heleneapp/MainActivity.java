package com.example.heleneapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;


import com.example.heleneapp.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView welcomeText;
    private TextView menuUsernameText, tvLangChip, cartBadge;
    private String username;
    private ArrayList<String> roles;
    private View menuHome, menuProfile, menuAdmin, menuInstagram, menuLogout, menuOrders, menuVideo;
    private LinearLayout menuIdioma;
    private ViewPager2 carousel;
    private TextView[] dots;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ImageButton cartButton, menuButton;
    private SearchView searchView;
    private ApiClient apiClient;
    private int cartCount = 0;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Long userId = UserSession.getInstance().getUserId();
        username = UserSession.getInstance().getUsername();

        if (userId == null || userId == -1) {
            Toast.makeText(this, "Por favor, inicia sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            username = "Usuario";
        }

        roles = new ArrayList<>(UserSession.getInstance().getRoles());
        if (roles == null) roles = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        cartButton = findViewById(R.id.cartButton);
        cartBadge = findViewById(R.id.cartBadge);
        searchView = findViewById(R.id.searchView);

        welcomeText = findViewById(R.id.welcomeText);
        menuUsernameText = findViewById(R.id.menuUsernameText);

        menuHome = findViewById(R.id.menuHome);
        menuProfile = findViewById(R.id.menuProfile);
        menuOrders = findViewById(R.id.menuOrders);
        menuAdmin = findViewById(R.id.menuAdmin);
        menuVideo = findViewById(R.id.menuVideo);
        menuInstagram = findViewById(R.id.menuInstagram);
        menuLogout = findViewById(R.id.menuLogout);
        menuIdioma = findViewById(R.id.menuIdioma);
        tvLangChip = findViewById(R.id.tvLangChip);

        apiClient = new ApiClient();

        welcomeText.setText(getString(R.string.bienvenido) + " " + username + "!");
        menuUsernameText.setText(username);

        if (roles.contains("ADMIN")) {
            menuAdmin.setVisibility(View.VISIBLE);
        } else {
            menuAdmin.setVisibility(View.GONE);
        }

        menuButton.setOnClickListener(v -> toggleDrawer());

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CarritoActivity.class);
            startActivity(intent);
        });

        loadCartCount();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Resultados");
                intent.putExtra("searchText", query);
                startActivity(intent);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        actualizarChipIdioma();

        setupMenuListeners();
        setupBackNavigation();

        carousel = findViewById(R.id.imageCarousel);
        int[] images = {R.drawable.img1, R.drawable.img2, R.drawable.img3};
        carousel.setAdapter(new ImageAdapter(images));

        LinearLayout dotsLayout = findViewById(R.id.dotsLayout);
        dots = setupDots(dotsLayout, images.length);

        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) { updateDots(dots, position); }
        });

        runnable = new Runnable() {
            int current = 0;
            @Override
            public void run() {
                if (current == images.length) current = 0;
                carousel.setCurrentItem(current++, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnable);

        View catFacial    = findViewById(R.id.catFacial);
        View catCorporal  = findViewById(R.id.catCorporal);
        View catProductos = findViewById(R.id.catProductos);
        View catOfertas   = findViewById(R.id.catOfertas);

        catFacial.setOnClickListener(v -> openCategory("Facial", 1L));
        catCorporal.setOnClickListener(v -> openCategory("Corporal", 2L));
        catProductos.setOnClickListener(v -> openCategory("Serums", 3L));
        catOfertas.setOnClickListener(v -> openCategory("Ofertas", 4L));
    }

    private void actualizarChipIdioma() {
        if (tvLangChip == null) return;
        String lang = LocaleHelper.getSavedLocale(this);
        tvLangChip.setText(lang.equals("en") ? "EN" : "ES");

        TextView tvIdiomaActual = findViewById(R.id.tvIdiomaActual);
        if (tvIdiomaActual != null) {
            tvIdiomaActual.setText(lang.equals("en") ? "English" : "Español");
        }
    }

    private void openCategory(String categoryName, Long categoriaId) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("category", categoryName);
        intent.putExtra("categoriaId", categoriaId);
        intent.putStringArrayListExtra("roles", roles);
        startActivity(intent);
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

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void setupMenuListeners() {
        menuHome.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show();
        });

        menuProfile.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, ProfileActivity.class));
        });

        menuOrders.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this, HistorialPedidosActivity.class));
        });

        menuAdmin.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, AdminActivity.class));
        });

        menuVideo.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, VideoActivity.class));
        });

        menuInstagram.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openInstagram();
        });

        menuIdioma.setOnClickListener(v -> {
            String actual = LocaleHelper.getSavedLocale(this);
            String nuevo  = actual.equals("es") ? "en" : "es";
            LocaleHelper.setLocale(this, nuevo);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("instagram://user?username=helene_cosmeticanatural"));
            if (intent.resolveActivity(getPackageManager()) == null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            }
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Salir")
                        .setMessage("¿Estás seguro?")
                        .setPositiveButton("Sí", (d, w) -> finishAffinity())
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private TextView[] setupDots(LinearLayout layout, int size) {
        TextView[] dots = new TextView[size];
        layout.removeAllViews();
        for (int i = 0; i < size; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("●");
            dots[i].setTextSize(18);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.gray));
            layout.addView(dots[i]);
        }
        if (size > 0) {
            dots[0].setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        return dots;
    }

    private void updateDots(TextView[] dots, int pos) {
        for (TextView d : dots) {
            d.setTextColor(ContextCompat.getColor(this, R.color.gray));
        }
        dots[pos].setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}