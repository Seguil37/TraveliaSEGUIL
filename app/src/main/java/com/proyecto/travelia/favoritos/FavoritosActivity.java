package com.proyecto.travelia.favoritos;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.R;
import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.data.local.FavoriteEntity;
import com.proyecto.travelia.data.session.UserSessionManager;
import com.proyecto.travelia.ui.BottomNavView;
import com.proyecto.travelia.ui.UserMenuHelper;

import java.util.List;

public class FavoritosActivity extends AppCompatActivity {

    private FavoritesRepository repo;
    private ReservationsRepository reservationsRepository;
    private UserSessionManager sessionManager;
    private FavoritosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);

        sessionManager = new UserSessionManager(this);
        if (!sessionManager.ensureLoggedIn(this)) {
            finish();
            return;
        }
        UserMenuHelper.bind(this, sessionManager);

        // Edge-to-edge: el BottomNav maneja el margen inferior
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, 0);
            return insets;
        });

        // Recycler
        repo = new FavoritesRepository(this, sessionManager);
        reservationsRepository = new ReservationsRepository(this, sessionManager);
        RecyclerView rv = findViewById(R.id.rvFavoritos);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new FavoritosAdapter(repo, this);
        rv.setAdapter(adapter);

        // Observa cambios de Room en tiempo real
        repo.observeAll().observe(this, (List<FavoriteEntity> list) -> {
            adapter.submit(list);
            TextView title = findViewById(R.id.tvTitle);
            if (title != null) title.setText("Mis favoritos (" + list.size() + ")");
            updateBadges(list != null ? list.size() : 0, null);
        });

        reservationsRepository.observeAll().observe(this, list -> {
            updateBadges(null, list != null ? list.size() : 0);
        });

        // BottomNav: acción especial para Agregar (opcional)
        BottomNavView bottom = findViewById(R.id.bottom_nav);
        if (bottom != null) {
            bottom.highlight(BottomNavView.Tab.FAVORITES);
        }
    }

    private void updateBadges(Integer fav, Integer res) {
        BottomNavView bottom = findViewById(R.id.bottom_nav);
        if (bottom == null) return;
        if (fav != null) bottom.setFavoritesBadge(fav);
        if (res != null) bottom.setReserveBadge(res);
    }
}
