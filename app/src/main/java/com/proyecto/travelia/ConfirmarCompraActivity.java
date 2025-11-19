package com.proyecto.travelia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.PurchasesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.data.local.PurchaseEntity;
import com.proyecto.travelia.data.session.UserSessionManager;
import com.proyecto.travelia.ui.BottomNavView;
import com.proyecto.travelia.ui.UserMenuHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfirmarCompraActivity extends AppCompatActivity {

    private Button btnVerViajes;
    private LinearLayout layoutCompras;
    private TextView tvEmpty;
    private TextView tvTotalPagado;
    private android.view.View viewDivider;

    private PurchasesRepository purchasesRepository;
    private ReservationsRepository reservationsRepository;
    private FavoritesRepository favoritesRepository;
    private UserSessionManager sessionManager;
    private BottomNavView bottomNav;
    private List<PurchaseEntity> currentPurchases = new ArrayList<>();
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmar_compra);

        sessionManager = new UserSessionManager(this);
        if (!sessionManager.ensureLoggedIn(this)) {
            finish();
            return;
        }
        UserMenuHelper.bind(this, sessionManager);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, 0);
            return insets;
        });

        initViews();
        setupBottomNavNew();
        initRepositories();
        setupListeners();
        setupBackPressedHandler();
    }

    private void initViews() {
        btnVerViajes = findViewById(R.id.btn_ver_viajes);
        layoutCompras = findViewById(R.id.layout_compra_reservas);
        tvEmpty = findViewById(R.id.tv_empty_compra);
        tvTotalPagado = findViewById(R.id.tv_total_pagado);
        viewDivider = findViewById(R.id.view_compra_divider);
        orderId = getIntent().getStringExtra("order_id");
        double totalIntent = getIntent().getDoubleExtra("total_reservas", 0d);
        tvTotalPagado.setText(String.format(Locale.getDefault(), "S/%.2f", totalIntent));
    }

    private void setupListeners() {
        btnVerViajes.setOnClickListener(v -> clearAndGoHome());
    }

    private void initRepositories() {
        purchasesRepository = new PurchasesRepository(this);
        reservationsRepository = new ReservationsRepository(this, sessionManager);
        favoritesRepository = new FavoritesRepository(this, sessionManager);

        if (orderId != null) {
            purchasesRepository.observeOrder(orderId).observe(this, this::renderResumen);
        } else {
            purchasesRepository.observeUser(sessionManager.getActiveUserId()).observe(this, this::renderResumen);
        }

        reservationsRepository.observeAll().observe(this, list ->
                updateBadges(null, list != null ? list.size() : 0));
        favoritesRepository.observeAll().observe(this, list ->
                updateBadges(list != null ? list.size() : 0, null));
    }

    private void renderResumen(List<PurchaseEntity> compras) {
        layoutCompras.removeAllViews();

        if (compras == null || compras.isEmpty()) {
            currentPurchases = new ArrayList<>();
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            viewDivider.setVisibility(android.view.View.GONE);
            tvTotalPagado.setText(String.format(Locale.getDefault(), "S/%.2f", 0f));
            return;
        }

        currentPurchases = new ArrayList<>(compras);
        tvEmpty.setVisibility(android.view.View.GONE);
        viewDivider.setVisibility(android.view.View.VISIBLE);

        double total = 0d;

        for (PurchaseEntity entity : currentPurchases) {
            android.view.View view = getLayoutInflater().inflate(R.layout.item_compra_resumen, layoutCompras, false);

            TextView tvTitulo = view.findViewById(R.id.tv_compra_titulo);
            TextView tvDetalle = view.findViewById(R.id.tv_compra_detalle);
            TextView tvSubtotal = view.findViewById(R.id.tv_compra_total);

            tvTitulo.setText(entity.title);
            String detalle = entity.location + " • " + entity.date + " • " + entity.participants;
            tvDetalle.setText(detalle);
            int participantes = entity.participantsCount <= 0 ? 1 : entity.participantsCount;
            tvSubtotal.setText(String.format(Locale.getDefault(), "S/%.2f x%d = S/%.2f",
                    entity.unitPrice,
                    participantes,
                    entity.totalPrice));

            layoutCompras.addView(view);
            total += entity.totalPrice;
        }

        tvTotalPagado.setText(String.format(Locale.getDefault(), "S/%.2f", total));
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                clearAndGoHome();
            }
        });
    }

    private void clearAndGoHome() {
        if (reservationsRepository != null) {
            reservationsRepository.clearAll();
        }
        Intent intent = new Intent(ConfirmarCompraActivity.this, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavNew() {
        bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav != null) {
            bottomNav.highlight(BottomNavView.Tab.RESERVE);
        }
    }

    private void updateBadges(Integer favCount, Integer resCount) {
        if (bottomNav == null) return;
        if (favCount != null) bottomNav.setFavoritesBadge(favCount);
        if (resCount != null) bottomNav.setReserveBadge(resCount);
    }
}
