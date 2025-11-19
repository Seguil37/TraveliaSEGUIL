package com.proyecto.travelia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.PurchasesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.data.UserRepository;
import com.proyecto.travelia.data.local.PurchaseEntity;
import com.proyecto.travelia.data.local.ReservationEntity;
import com.proyecto.travelia.data.local.UserEntity;
import com.proyecto.travelia.data.session.UserSessionManager;
import com.proyecto.travelia.favoritos.FavoritosActivity;
import com.proyecto.travelia.ui.BottomNavView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etTelefono;
    private Spinner spPais;
    private TextView tvFavoritos, tvReservas, tvCompras;
    private LinearLayout layoutReservas, layoutCompras;

    private UserRepository userRepository;
    private ReservationsRepository reservationsRepository;
    private PurchasesRepository purchasesRepository;
    private FavoritesRepository favoritesRepository;
    private UserSessionManager sessionManager;

    private UserEntity currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        sessionManager = new UserSessionManager(this);
        if (!sessionManager.ensureLoggedIn(this)) {
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, 0);
            return insets;
        });

        userRepository = new UserRepository(this);
        reservationsRepository = new ReservationsRepository(this, sessionManager);
        purchasesRepository = new PurchasesRepository(this);
        favoritesRepository = new com.proyecto.travelia.data.FavoritesRepository(this, sessionManager);

        initViews();
        setupSpinner();
        setupBottomNav();
        setupButtons();
        observeData();
    }

    private void initViews() {
        etNombre = findViewById(R.id.et_perfil_nombre);
        etEmail = findViewById(R.id.et_perfil_email);
        etTelefono = findViewById(R.id.et_perfil_telefono);
        spPais = findViewById(R.id.sp_perfil_pais);
        tvFavoritos = findViewById(R.id.tv_count_favoritos);
        tvReservas = findViewById(R.id.tv_count_reservas);
        tvCompras = findViewById(R.id.tv_count_compras);
        layoutReservas = findViewById(R.id.layout_reservas_perfil);
        layoutCompras = findViewById(R.id.layout_compras_recientes);
    }

    private void setupSpinner() {
        String[] paises = {"Perú", "Chile", "Argentina", "Colombia", "México", "España"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paises);
        spPais.setAdapter(adapter);
    }

    private void setupBottomNav() {
        BottomNavView bottom = findViewById(R.id.bottom_nav);
        bottom.highlight(BottomNavView.Tab.PROFILE);
    }

    private void setupButtons() {
        Button btnGuardar = findViewById(R.id.btn_guardar_perfil);
        btnGuardar.setOnClickListener(v -> guardarPerfil());

        findViewById(R.id.btn_ver_favoritos).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritosActivity.class)));

        findViewById(R.id.btn_ver_reservas).setOnClickListener(v ->
                startActivity(new Intent(this, ConfirmarReservaActivity.class)));

        findViewById(R.id.btn_ver_compras).setOnClickListener(v ->
                startActivity(new Intent(this, HistorialComprasActivity.class)));

        findViewById(R.id.btn_cerrar_sesion).setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }

    private void observeData() {
        String userId = sessionManager.getActiveUserId();
        LiveData<UserEntity> userLiveData = userRepository.observeUser(userId);
        userLiveData.observe(this, user -> {
            if (user == null) return;
            currentUser = user;
            etNombre.setText(user.name);
            etEmail.setText(user.email);
            etTelefono.setText(user.phone);
            selectPais(user.nationality);
        });

        favoritesRepository.observeAll().observe(this, list -> {
            tvFavoritos.setText(String.valueOf(list != null ? list.size() : 0));
            updateBadges(list != null ? list.size() : 0, null);
        });

        reservationsRepository.observeAll().observe(this, reservations -> {
            int count = reservations != null ? reservations.size() : 0;
            tvReservas.setText(String.valueOf(count));
            updateBadges(null, count);
            renderReservas(reservations);
        });

        purchasesRepository.observeUser(userId).observe(this, purchases -> {
            int count = purchases != null ? purchases.size() : 0;
            tvCompras.setText(String.valueOf(count));
            renderCompras(purchases);
        });
    }

    private void renderReservas(List<ReservationEntity> reservas) {
        layoutReservas.removeAllViews();
        if (reservas == null || reservas.isEmpty()) {
            addEmptyText(layoutReservas, "No tienes reservas activas");
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        int limit = Math.min(reservas.size(), 2);
        for (int i = 0; i < limit; i++) {
            ReservationEntity entity = reservas.get(i);
            View view = inflater.inflate(R.layout.item_reserva, layoutReservas, false);
            ((TextView) view.findViewById(R.id.tv_reserva_titulo)).setText(entity.title);
            ((TextView) view.findViewById(R.id.tv_reserva_ubicacion)).setText(entity.location);
            ((TextView) view.findViewById(R.id.tv_reserva_fecha)).setText(entity.date);
            ((TextView) view.findViewById(R.id.tv_reserva_participantes)).setText(entity.participants);
            ((TextView) view.findViewById(R.id.tv_reserva_unitario)).setText(
                    String.format(Locale.getDefault(), "Unitario: S/%.2f", entity.unitPrice));
            ((TextView) view.findViewById(R.id.tv_reserva_precio)).setText(
                    String.format(Locale.getDefault(), "Subtotal: S/%.2f", entity.price));
            view.findViewById(R.id.btn_eliminar_reserva).setVisibility(View.GONE);
            layoutReservas.addView(view);
        }
    }

    private void renderCompras(List<PurchaseEntity> compras) {
        layoutCompras.removeAllViews();
        if (compras == null || compras.isEmpty()) {
            addEmptyText(layoutCompras, "Aún no registras compras");
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        int limit = Math.min(compras.size(), 2);
        for (int i = 0; i < limit; i++) {
            PurchaseEntity entity = compras.get(i);
            View view = inflater.inflate(R.layout.item_compra_resumen, layoutCompras, false);
            ((TextView) view.findViewById(R.id.tv_compra_titulo)).setText(entity.title);
            String detalle = entity.date + " • " + entity.participants;
            ((TextView) view.findViewById(R.id.tv_compra_detalle)).setText(detalle);
            ((TextView) view.findViewById(R.id.tv_compra_total)).setText(
                    String.format(Locale.getDefault(), "Total: S/%.2f", entity.totalPrice));
            layoutCompras.addView(view);
        }
    }

    private void addEmptyText(LinearLayout container, String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(getResources().getColor(R.color.travelia_text_muted));
        container.addView(tv);
    }

    private void guardarPerfil() {
        if (currentUser == null) return;
        currentUser.name = etNombre.getText().toString().trim();
        currentUser.phone = etTelefono.getText().toString().trim();
        currentUser.nationality = spPais.getSelectedItem().toString();
        userRepository.update(currentUser, (success, message, user) -> {
            if (success) {
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, message != null ? message : "No se pudo actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectPais(String nationality) {
        if (nationality == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spPais.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (nationality.equals(adapter.getItem(i))) {
                spPais.setSelection(i);
                break;
            }
        }
    }

    private void updateBadges(Integer favCount, Integer resCount) {
        BottomNavView bottom = findViewById(R.id.bottom_nav);
        if (favCount != null) bottom.setFavoritesBadge(favCount);
        if (resCount != null) bottom.setReserveBadge(resCount);
    }
}
