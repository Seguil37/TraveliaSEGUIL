package com.proyecto.travelia;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.compras.ComprasAdapter;
import com.proyecto.travelia.data.PurchasesRepository;
import com.proyecto.travelia.data.local.PurchaseEntity;
import com.proyecto.travelia.data.session.UserSessionManager;
import com.proyecto.travelia.ui.BottomNavView;
import com.proyecto.travelia.ui.UserMenuHelper;

import java.util.List;

public class HistorialComprasActivity extends AppCompatActivity {

    private ComprasAdapter adapter;
    private PurchasesRepository purchasesRepository;
    private UserSessionManager sessionManager;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial_compras);

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

        BottomNavView bottom = findViewById(R.id.bottom_nav);
        bottom.highlight(BottomNavView.Tab.PROFILE);

        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rv = findViewById(R.id.rvCompras);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComprasAdapter();
        rv.setAdapter(adapter);

        purchasesRepository = new PurchasesRepository(this);
        purchasesRepository.observeUser(sessionManager.getActiveUserId()).observe(this, this::render);
    }

    private void render(List<PurchaseEntity> purchases) {
        boolean empty = purchases == null || purchases.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        adapter.submit(purchases);
    }
}
