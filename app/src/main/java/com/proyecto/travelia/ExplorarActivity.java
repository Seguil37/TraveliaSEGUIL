package com.proyecto.travelia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.data.local.FavoriteEntity;
import com.proyecto.travelia.data.local.ReservationEntity;
import com.proyecto.travelia.data.session.UserSessionManager;
import com.proyecto.travelia.explorar.FilterOptions;
import com.proyecto.travelia.explorar.Tour;
import com.proyecto.travelia.explorar.ToursAdapter;
import com.proyecto.travelia.ui.BottomNavView;
import com.proyecto.travelia.ui.UserMenuHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ExplorarActivity extends AppCompatActivity implements ToursAdapter.TourActionListener {

    private ToursAdapter adapter;
    private final List<Tour> allTours = new ArrayList<>();
    private final FilterOptions filters = new FilterOptions();
    private final Set<String> favoriteIds = new HashSet<>();

    private FavoritesRepository favoritesRepository;
    private ReservationsRepository reservationsRepository;
    private UserSessionManager sessionManager;

    private TextView tvContador;
    private TextView tvFiltros;
    private Spinner spOrden;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explorar);

        sessionManager = new UserSessionManager(this);
        favoritesRepository = new FavoritesRepository(this, sessionManager);
        reservationsRepository = new ReservationsRepository(this, sessionManager);
        UserMenuHelper.bind(this, sessionManager);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, 0);
            return insets;
        });

        initViews();
        setupRecycler();
        setupFilters();
        observeRepositories();
        populateTours();
        applyFilters();
    }

    private void initViews() {
        tvContador = findViewById(R.id.tv_contador);
        tvFiltros = findViewById(R.id.tv_filtros_activos);
        spOrden = findViewById(R.id.sp_orden);
        etSearch = findViewById(R.id.et_search);

        BottomNavView bottom = findViewById(R.id.bottom_nav);
        bottom.highlight(BottomNavView.Tab.EXPLORAR);

        Button btnFiltros = findViewById(R.id.btn_filtros);
        btnFiltros.setOnClickListener(v -> mostrarDialogoFiltros());

        findViewById(R.id.btn_mas_rutas).setOnClickListener(v -> {
            filters.destino = "";
            filters.tipo = "";
            filters.fecha = "";
            filters.maxPrecio = 2000;
            applyFilters();
        });
    }

    private void setupRecycler() {
        RecyclerView rv = findViewById(R.id.rv_destinos);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ToursAdapter();
        adapter.setListener(this);
        rv.setAdapter(adapter);
    }

    private void setupFilters() {
        String[] opciones = {"Nombre", "Precio", "Rating"};
        spOrden.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones));
        spOrden.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { applyFilters(); }
        });
    }

    private void observeRepositories() {
        favoritesRepository.observeAll().observe(this, list -> {
            favoriteIds.clear();
            if (list != null) {
                for (FavoriteEntity entity : list) {
                    favoriteIds.add(entity.itemId);
                }
            }
            adapter.setFavoriteIds(favoriteIds);
            updateBadges(list != null ? list.size() : 0, null);
        });

        reservationsRepository.observeAll().observe(this, reservations -> {
            int count = reservations != null ? reservations.size() : 0;
            updateBadges(null, count);
        });
    }

    private void updateBadges(Integer fav, Integer res) {
        BottomNavView bottom = findViewById(R.id.bottom_nav);
        if (fav != null) bottom.setFavoritesBadge(fav);
        if (res != null) bottom.setReserveBadge(res);
    }

    private void populateTours() {
        allTours.clear();
        allTours.add(new Tour("T-001", "Machu Picchu Full Day", "Cusco, Perú", "Aventura", 280, 4.8f, R.drawable.mapi, "10/11/2024", "12/11/2024"));
        allTours.add(new Tour("T-002", "Lago Titicaca", "Puno, Perú", "Cultural", 380, 4.7f, R.drawable.lagotiticaca, "05/12/2024"));
        allTours.add(new Tour("T-003", "Montaña de 7 Colores", "Cusco, Perú", "Trekking", 350, 4.6f, R.drawable.montanacolores, "08/11/2024"));
        allTours.add(new Tour("T-004", "Selva amazónica", "Madre de Dios", "Naturaleza", 420, 4.5f, R.drawable.mapi, "15/01/2025"));
        allTours.add(new Tour("T-005", "Islas Ballestas", "Ica, Perú", "Relax", 180, 4.4f, R.drawable.lagotiticaca, "20/11/2024"));
    }

    private void applyFilters() {
        String search = etSearch.getText().toString().toLowerCase(Locale.getDefault());
        List<Tour> filtered = new ArrayList<>();
        for (Tour tour : allTours) {
            if (!search.isEmpty() && !tour.nombre.toLowerCase(Locale.getDefault()).contains(search)
                    && !tour.destino.toLowerCase(Locale.getDefault()).contains(search)) continue;
            if (!filters.destino.isEmpty() && !tour.destino.toLowerCase(Locale.getDefault()).contains(filters.destino.toLowerCase(Locale.getDefault()))) continue;
            if (!filters.tipo.isEmpty() && !tour.tipo.equalsIgnoreCase(filters.tipo)) continue;
            if (tour.precio > filters.maxPrecio) continue;
            if (!filters.fecha.isEmpty() && !tour.fechas.contains(filters.fecha)) continue;
            filtered.add(tour);
        }

        sortTours(filtered, spOrden.getSelectedItemPosition());
        adapter.submit(filtered);
        tvContador.setText(String.format(Locale.getDefault(), "%d experiencias encontradas", filtered.size()));
        updateFiltrosResumen();
    }

    private void sortTours(List<Tour> tours, int modo) {
        tours.sort((t1, t2) -> {
            switch (modo) {
                case 1:
                    return Double.compare(t1.precio, t2.precio);
                case 2:
                    return Float.compare(t2.rating, t1.rating);
                default:
                    return t1.nombre.compareToIgnoreCase(t2.nombre);
            }
        });
    }

    private void updateFiltrosResumen() {
        List<String> activos = new ArrayList<>();
        if (!filters.destino.isEmpty()) activos.add("Destino: " + filters.destino);
        if (!filters.tipo.isEmpty()) activos.add("Tipo: " + filters.tipo);
        if (!filters.fecha.isEmpty()) activos.add("Fecha: " + filters.fecha);
        if (filters.maxPrecio < 2000) activos.add("<= S/" + filters.maxPrecio);
        if (activos.isEmpty()) {
            tvFiltros.setVisibility(View.GONE);
        } else {
            tvFiltros.setVisibility(View.VISIBLE);
            tvFiltros.setText(TextUtils.join(" • ", activos));
        }
    }

    private void mostrarDialogoFiltros() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_filters, null, false);
        EditText etDestino = view.findViewById(R.id.et_filter_destino);
        Spinner spTipo = view.findViewById(R.id.sp_filter_tipo);
        SeekBar seekPrecio = view.findViewById(R.id.seek_filter_precio);
        TextView tvPrecio = view.findViewById(R.id.tv_filter_precio);
        TextView tvFecha = view.findViewById(R.id.tv_filter_fecha);

        String[] tipos = {"", "Aventura", "Cultural", "Trekking", "Relax", "Naturaleza"};
        spTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));

        etDestino.setText(filters.destino);
        seekPrecio.setProgress(filters.maxPrecio);
        tvPrecio.setText(String.format(Locale.getDefault(), "Precio máximo: S/%d", filters.maxPrecio));
        tvFecha.setText(filters.fecha.isEmpty() ? "Seleccionar fecha" : filters.fecha);
        int tipoIndex = java.util.Arrays.asList(tipos).indexOf(filters.tipo);
        if (tipoIndex >= 0) spTipo.setSelection(tipoIndex);

        seekPrecio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPrecio.setText(String.format(Locale.getDefault(), "Precio máximo: S/%d", progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        tvFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
                String fecha = day + "/" + (month + 1) + "/" + year;
                tvFecha.setText(fecha);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        view.findViewById(R.id.btn_filter_aplicar).setOnClickListener(v -> {
            filters.destino = etDestino.getText().toString().trim();
            filters.tipo = spTipo.getSelectedItem().toString();
            filters.maxPrecio = seekPrecio.getProgress();
            filters.fecha = tvFecha.getText().toString().equals("Seleccionar fecha") ? "" : tvFecha.getText().toString();
            dialog.dismiss();
            applyFilters();
        });

        view.findViewById(R.id.btn_filter_limpiar).setOnClickListener(v -> {
            filters.destino = "";
            filters.tipo = "";
            filters.fecha = "";
            filters.maxPrecio = 2000;
            dialog.dismiss();
            applyFilters();
        });

        dialog.show();
    }

    @Override
    public void onDetalles(Tour tour) {
        Intent intent = new Intent(this, DetalleArticuloActivity.class);
        intent.putExtra("id", tour.id);
        intent.putExtra("titulo", tour.nombre);
        intent.putExtra("ubicacion", tour.destino);
        intent.putExtra("precio", String.format(Locale.getDefault(), "S/%.2f", tour.precio));
        intent.putExtra("rating", String.valueOf(tour.rating));
        intent.putExtra("imageRes", tour.imageRes);
        startActivity(intent);
    }

    @Override
    public void onFavorite(Tour tour) {
        if (!sessionManager.ensureLoggedIn(this)) return;
        boolean isFavorite = favoriteIds.contains(tour.id);
        if (isFavorite) {
            favoritesRepository.remove(tour.id);
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        } else {
            FavoriteEntity entity = new FavoriteEntity(sessionManager.getActiveOrGuestId(), tour.id, "TOUR",
                    tour.nombre, tour.destino, "", tour.precio, (double) tour.rating, System.currentTimeMillis());
            favoritesRepository.add(entity);
            Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
        }
    }
}
