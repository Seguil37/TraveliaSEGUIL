package com.proyecto.travelia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.PurchaseRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.data.SessionManager;
import com.proyecto.travelia.data.UserRepository;
import com.proyecto.travelia.data.local.FavoriteEntity;
import com.proyecto.travelia.data.local.PurchaseEntity;
import com.proyecto.travelia.data.local.ReservationEntity;
import com.proyecto.travelia.data.local.UserEntity;

import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private TextView tvSessionMessage;
    private TextView tvFavoritesCount;
    private TextView tvReservationsCount;
    private TextView tvPurchasesCount;
    private LinearLayout layoutFavoritesPreview;
    private LinearLayout layoutReservationsPreview;
    private LinearLayout layoutPurchasesPreview;
    private Button btnGuardarPerfil;
    private Button btnLogout;
    private Button btnIrFavoritos;
    private Button btnIrReservas;
    private Button btnIrCompras;

    private UserRepository userRepository;
    private FavoritesRepository favoritesRepository;
    private ReservationsRepository reservationsRepository;
    private PurchaseRepository purchaseRepository;
    private SessionManager sessionManager;

    private LiveData<UserEntity> activeUserLiveData;
    private LiveData<List<FavoriteEntity>> favoritesLiveData;
    private LiveData<List<ReservationEntity>> reservationsLiveData;
    private LiveData<List<PurchaseEntity>> purchasesLiveData;

    private String activeUserKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        userRepository = new UserRepository(this);
        favoritesRepository = new FavoritesRepository(this);
        reservationsRepository = new ReservationsRepository(this);
        purchaseRepository = new PurchaseRepository(this);
        sessionManager = userRepository.getSessionManager();

        initViews();
        setupActions();
        observeSession();
    }

    private void initViews() {
        etName = findViewById(R.id.tv_profile_name);
        etEmail = findViewById(R.id.tv_profile_email);
        etPhone = findViewById(R.id.tv_profile_phone);
        tvSessionMessage = findViewById(R.id.tv_profile_session_state);
        tvFavoritesCount = findViewById(R.id.tv_profile_favorites_count);
        tvReservationsCount = findViewById(R.id.tv_profile_reservations_count);
        tvPurchasesCount = findViewById(R.id.tv_profile_purchases_count);
        layoutFavoritesPreview = findViewById(R.id.layout_profile_favorites);
        layoutReservationsPreview = findViewById(R.id.layout_profile_reservas);
        layoutPurchasesPreview = findViewById(R.id.layout_profile_compras);
        btnGuardarPerfil = findViewById(R.id.btn_guardar_perfil);
        btnLogout = findViewById(R.id.btn_logout);
        btnIrFavoritos = findViewById(R.id.btn_ver_favoritos);
        btnIrReservas = findViewById(R.id.btn_ver_reservas);
        btnIrCompras = findViewById(R.id.btn_ver_compras);
    }

    private void setupActions() {
        btnGuardarPerfil.setOnClickListener(v -> {
            if (activeUserKey == null) {
                Toast.makeText(this, "Inicia sesión para actualizar tu perfil", Toast.LENGTH_SHORT).show();
                return;
            }
            String nombre = etName.getText().toString().trim();
            String telefono = etPhone.getText().toString().trim();
            userRepository.updateProfile(nombre, telefono, (success, user, message) ->
                    Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show());
        });

        btnLogout.setOnClickListener(v -> {
            userRepository.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });

        btnIrFavoritos.setOnClickListener(v -> {
            if (activeUserKey == null) {
                irALogin();
            } else {
                startActivity(new Intent(ProfileActivity.this, com.proyecto.travelia.favoritos.FavoritosActivity.class));
            }
        });

        btnIrReservas.setOnClickListener(v -> {
            if (activeUserKey == null) {
                irALogin();
            } else {
                startActivity(new Intent(ProfileActivity.this, ConfirmarReservaActivity.class));
            }
        });

        btnIrCompras.setOnClickListener(v -> {
            if (activeUserKey == null) {
                irALogin();
            } else {
                startActivity(new Intent(ProfileActivity.this, ConfirmarCompraActivity.class));
            }
        });
    }

    private void observeSession() {
        sessionManager.getActiveUserId().observe(this, userId -> {
            detachSources();
            if (userId == null) {
                activeUserKey = null;
                showNoSession();
            } else {
                activeUserKey = String.valueOf(userId);
                loadData(userId);
            }
        });
    }

    private void loadData(long userId) {
        tvSessionMessage.setVisibility(View.GONE);
        btnGuardarPerfil.setEnabled(true);
        btnLogout.setEnabled(true);

        activeUserLiveData = userRepository.observeUser(userId);
        activeUserLiveData.observe(this, user -> {
            if (user == null) {
                showNoSession();
                return;
            }
            etName.setText(user.name != null ? user.name : "");
            etEmail.setText(user.email != null ? user.email : "");
            etPhone.setText(user.phone != null ? user.phone : "");
        });

        favoritesLiveData = favoritesRepository.observeByUser(activeUserKey);
        favoritesLiveData.observe(this, this::renderFavorites);

        reservationsLiveData = reservationsRepository.observeByUser(activeUserKey);
        reservationsLiveData.observe(this, this::renderReservations);

        purchasesLiveData = purchaseRepository.observeByUser(activeUserKey);
        purchasesLiveData.observe(this, this::renderPurchases);
    }

    private void renderFavorites(List<FavoriteEntity> list) {
        if (list == null) list = Collections.emptyList();
        tvFavoritesCount.setText(String.valueOf(list.size()));
        layoutFavoritesPreview.removeAllViews();
        if (list.isEmpty()) {
            addEmptyState(layoutFavoritesPreview);
            return;
        }
        for (int i = 0; i < Math.min(list.size(), 3); i++) {
            FavoriteEntity entity = list.get(i);
            addPreviewRow(layoutFavoritesPreview, entity.title + " • " + entity.location);
        }
    }

    private void renderReservations(List<ReservationEntity> list) {
        if (list == null) list = Collections.emptyList();
        tvReservationsCount.setText(String.valueOf(list.size()));
        layoutReservationsPreview.removeAllViews();
        if (list.isEmpty()) {
            addEmptyState(layoutReservationsPreview);
            return;
        }
        for (int i = 0; i < Math.min(list.size(), 3); i++) {
            ReservationEntity entity = list.get(i);
            addPreviewRow(layoutReservationsPreview, entity.title + " • " + entity.date);
        }
    }

    private void renderPurchases(List<PurchaseEntity> list) {
        if (list == null) list = Collections.emptyList();
        tvPurchasesCount.setText(String.valueOf(list.size()));
        layoutPurchasesPreview.removeAllViews();
        if (list.isEmpty()) {
            addEmptyState(layoutPurchasesPreview);
            return;
        }
        for (int i = 0; i < Math.min(list.size(), 3); i++) {
            PurchaseEntity entity = list.get(i);
            addPreviewRow(layoutPurchasesPreview, entity.title + " • S/" + entity.price);
        }
    }

    private void addPreviewRow(LinearLayout container, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        tv.setTextSize(14f);
        container.addView(tv);
    }

    private void addEmptyState(LinearLayout container) {
        TextView tv = new TextView(this);
        tv.setText("Sin datos recientes");
        tv.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        container.addView(tv);
    }

    private void showNoSession() {
        tvSessionMessage.setVisibility(View.VISIBLE);
        tvSessionMessage.setOnClickListener(v -> irALogin());
        tvFavoritesCount.setText("0");
        tvReservationsCount.setText("0");
        tvPurchasesCount.setText("0");
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        layoutFavoritesPreview.removeAllViews();
        layoutReservationsPreview.removeAllViews();
        layoutPurchasesPreview.removeAllViews();
        btnGuardarPerfil.setEnabled(false);
        btnLogout.setEnabled(false);
    }

    private void detachSources() {
        if (activeUserLiveData != null) {
            activeUserLiveData.removeObservers(this);
            activeUserLiveData = null;
        }
        if (favoritesLiveData != null) {
            favoritesLiveData.removeObservers(this);
            favoritesLiveData = null;
        }
        if (reservationsLiveData != null) {
            reservationsLiveData.removeObservers(this);
            reservationsLiveData = null;
        }
        if (purchasesLiveData != null) {
            purchasesLiveData.removeObservers(this);
            purchasesLiveData = null;
        }
    }

    private void irALogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
