package com.proyecto.travelia;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.data.FavoritesRepository;
import com.proyecto.travelia.data.ReservationsRepository;
import com.proyecto.travelia.data.ReviewRepository;
import com.proyecto.travelia.data.UserRepository;
import com.proyecto.travelia.data.local.ReservationEntity;
import com.proyecto.travelia.data.local.UserEntity;
import com.proyecto.travelia.data.session.UserSessionManager;
import com.proyecto.travelia.reviews.ReviewsAdapter;
import com.proyecto.travelia.ui.BottomNavView;

import java.util.Calendar;
import java.util.Locale;

public class DetalleArticuloActivity extends AppCompatActivity {

    private TextView tvTituloDetalle, tvValoracion, tvDuracion, tvIncluye;
    private TextView tvServicios, tvIdiomas, tvUbicacionDetalle, tvDescripcion;
    private TextView tvPrecioDetalle, tvFecha, tvPromedioResenas, tvTotalResenas;
    private ImageView ivDetalleImagen;
    private Spinner spAdultos, spIdioma;
    private Button btnRegresar, btnVerDisponibilidad, btnReservar;
    private Button btnEscribirOpinion, btnVerMasResenas;
    private RatingBar rbPromedio;
    private RecyclerView rvReviews;
    private BottomNavView bottomNav;

    private ReservationsRepository reservationsRepository;
    private FavoritesRepository favoritesRepository;
    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private UserSessionManager sessionManager;
    private ReviewsAdapter reviewsAdapter;
    private UserEntity currentUser;

    private String tourId;
    private int imageRes;
    private double priceValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_articulo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, 0);
            return insets;
        });

        sessionManager = new UserSessionManager(this);
        reservationsRepository = new ReservationsRepository(this, sessionManager);
        favoritesRepository = new FavoritesRepository(this, sessionManager);
        reviewRepository = new ReviewRepository(this);
        userRepository = new UserRepository(this);

        String activeUserId = sessionManager.getActiveUserId();
        if (activeUserId != null) {
            userRepository.observeUser(activeUserId).observe(this, user -> currentUser = user);
        }

        initViews();
        setupBottomNavNew();
        loadArticleData();
        setupReviewsSection();
        setupSpinners();
        setupListeners();
        observeBadges();
    }

    private void initViews() {
        tvTituloDetalle = findViewById(R.id.tv_titulo_detalle);
        tvValoracion = findViewById(R.id.tv_valoracion);
        tvDuracion = findViewById(R.id.tv_duracion);
        tvIncluye = findViewById(R.id.tv_incluye);
        tvServicios = findViewById(R.id.tv_servicios);
        tvIdiomas = findViewById(R.id.tv_idiomas);
        tvUbicacionDetalle = findViewById(R.id.tv_ubicacion_detalle);
        tvDescripcion = findViewById(R.id.tv_descripcion);
        tvPrecioDetalle = findViewById(R.id.tv_precio_detalle);
        tvFecha = findViewById(R.id.tv_fecha);
        tvPromedioResenas = findViewById(R.id.tv_promedio_resenas);
        tvTotalResenas = findViewById(R.id.tv_total_resenas);
        rbPromedio = findViewById(R.id.rb_promedio);
        ivDetalleImagen = findViewById(R.id.iv_detalle_imagen);

        spAdultos = findViewById(R.id.sp_adultos);
        spIdioma = findViewById(R.id.sp_idioma);

        btnRegresar = findViewById(R.id.btn_regresar);
        btnVerDisponibilidad = findViewById(R.id.btn_ver_disponibilidad);
        btnReservar = findViewById(R.id.btn_reservar);
        btnEscribirOpinion = findViewById(R.id.btn_escribir_opinion);
        btnVerMasResenas = findViewById(R.id.btn_ver_mas_resenas);

        rvReviews = findViewById(R.id.rv_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewsAdapter = new ReviewsAdapter();
        rvReviews.setAdapter(reviewsAdapter);
    }

    private void loadArticleData() {
        Intent intent = getIntent();
        tourId = intent.getStringExtra("id");
        String titulo = intent.getStringExtra("titulo");
        String ubicacion = intent.getStringExtra("ubicacion");
        String precio = intent.getStringExtra("precio");
        String rating = intent.getStringExtra("rating");
        imageRes = intent.getIntExtra("imageRes", R.drawable.mapi);

        if (tourId == null && titulo != null) {
            tourId = titulo.replaceAll("\\s+", "-").toLowerCase(Locale.getDefault());
        }
        if (tourId == null) {
            tourId = "tour-demo";
        }

        if (titulo != null) tvTituloDetalle.setText(titulo);
        if (ubicacion != null) tvUbicacionDetalle.setText(ubicacion);
        priceValue = parsePrice(precio);
        tvPrecioDetalle.setText(String.format(Locale.getDefault(), "S/%.2f", priceValue));
        if (rating != null) tvValoracion.setText("★ " + rating);
        ivDetalleImagen.setImageResource(imageRes);

        tvDuracion.setText("1 Día Completo");
        tvIncluye.setText("Transporte, guía, entradas");
        tvServicios.setText("Almuerzo incluido");
        tvIdiomas.setText("Español, Inglés");
        tvDescripcion.setText("Descubre una de las maravillas del mundo en un tour único que combina historia, cultura y aventura. Perfecto para quienes buscan una experiencia inolvidable en los Andes.");
    }

    private void setupSpinners() {
        String[] adultos = {"Adulto x 1", "Adultos x 2", "Adultos x 3", "Adultos x 4"};
        ArrayAdapter<String> adultosAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, adultos);
        adultosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAdultos.setAdapter(adultosAdapter);

        String[] idiomas = {"Español", "Inglés", "Francés", "Alemán"};
        ArrayAdapter<String> idiomasAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, idiomas);
        idiomasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIdioma.setAdapter(idiomasAdapter);
    }

    private void setupReviewsSection() {
        if (reviewRepository == null || tourId == null) return;
        reviewRepository.observe(tourId).observe(this, reviews -> {
            reviewsAdapter.submit(reviews);
            int count = reviews != null ? reviews.size() : 0;
            tvTotalResenas.setText(getString(R.string.review_total_format, count));
        });
        reviewRepository.observeAverage(tourId).observe(this, average -> {
            double value = average != null ? average : 0d;
            tvPromedioResenas.setText(String.format(Locale.getDefault(), "%.1f", value));
            rbPromedio.setRating((float) value);
        });
        reviewRepository.seedIfNeeded(tourId);
    }

    private void setupListeners() {
        btnRegresar.setOnClickListener(v -> finish());

        btnVerDisponibilidad.setOnClickListener(v -> {
            findViewById(R.id.sp_adultos).requestFocus();
            Toast.makeText(this, "Selecciona fecha y participantes", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_seleccionar_fecha).setOnClickListener(v -> showDatePicker());

        btnReservar.setOnClickListener(v -> {
            if (!sessionManager.ensureLoggedIn(this)) return;
            String fecha = tvFecha.getText().toString();
            if (getString(R.string.detalle_fecha_placeholder).contentEquals(fecha)) {
                Toast.makeText(this, "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }
            String titulo = tvTituloDetalle.getText().toString();
            String ubicacion = tvUbicacionDetalle.getText().toString();
            String participantesBase = spAdultos.getSelectedItem().toString();
            int participantesCount = parseParticipantsCount(participantesBase);
            String idioma = spIdioma.getSelectedItem().toString();
            String participantes = participantesBase + " • " + idioma;
            double subtotal = priceValue * participantesCount;
            String reservationId = ReservationsRepository.buildId(tourId != null ? tourId : titulo, fecha);

            ReservationEntity entity = new ReservationEntity(
                    reservationId,
                    sessionManager.getActiveUserId(),
                    tourId,
                    titulo,
                    ubicacion,
                    fecha,
                    participantes,
                    participantesCount,
                    priceValue,
                    subtotal,
                    imageRes,
                    System.currentTimeMillis()
            );

            reservationsRepository.upsert(entity);

            Intent intent = new Intent(DetalleArticuloActivity.this, ConfirmarReservaActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Agregado a tu carrito de reservas", Toast.LENGTH_SHORT).show();
        });

        btnEscribirOpinion.setOnClickListener(v -> showReviewDialog());

        btnVerMasResenas.setOnClickListener(v -> {
            if (reviewsAdapter.getItemCount() > 0) {
                rvReviews.smoothScrollToPosition(reviewsAdapter.getItemCount() - 1);
            } else {
                Toast.makeText(this, R.string.no_reviews_yet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String fecha = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvFecha.setText(fecha);
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showReviewDialog() {
        if (!sessionManager.ensureLoggedIn(this)) return;
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null, false);
        RatingBar ratingBar = view.findViewById(R.id.dialog_rating);
        EditText etComment = view.findViewById(R.id.dialog_comment);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        view.findViewById(R.id.dialog_submit).setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            if (rating <= 0f) {
                Toast.makeText(this, R.string.review_rating_required, Toast.LENGTH_SHORT).show();
                return;
            }
            String comment = etComment.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, R.string.review_comment_required, Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = sessionManager.getActiveUserId();
            if (userId == null) {
                userId = UserSessionManager.ANONYMOUS_USER_ID;
            }
            String userName = currentUser != null && currentUser.name != null
                    ? currentUser.name
                    : getString(R.string.default_user_name);
            reviewRepository.addReview(tourId, userId, userName, rating, comment);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void setupBottomNavNew() {
        bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav != null) {
            bottomNav.highlight(BottomNavView.Tab.EXPLORAR);
        }
    }

    private void observeBadges() {
        if (favoritesRepository != null) {
            favoritesRepository.observeAll().observe(this, list ->
                    updateBadges(list != null ? list.size() : 0, null));
        }
        reservationsRepository.observeAll().observe(this, list ->
                updateBadges(null, list != null ? list.size() : 0));
    }

    private void updateBadges(Integer favCount, Integer resCount) {
        if (bottomNav == null) return;
        if (favCount != null) bottomNav.setFavoritesBadge(favCount);
        if (resCount != null) bottomNav.setReserveBadge(resCount);
    }

    private double parsePrice(String price) {
        if (price == null) return 0d;
        String cleaned = price.replace("S/", "").replace("s/", "");
        cleaned = cleaned.replaceAll("[^0-9.,]", "").replace(",", "");
        if (cleaned.isEmpty()) return 0d;
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    private int parseParticipantsCount(String text) {
        if (text == null) return 1;
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 1;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
