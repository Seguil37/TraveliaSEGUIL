package com.proyecto.travelia.explorar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ToursAdapter extends RecyclerView.Adapter<ToursAdapter.VH> {

    public interface TourActionListener {
        void onDetalles(Tour tour);
        void onFavorite(Tour tour);
    }

    private final List<Tour> data = new ArrayList<>();
    private final Set<String> favoriteIds = new HashSet<>();
    private TourActionListener listener;

    public void submit(List<Tour> tours) {
        data.clear();
        if (tours != null) data.addAll(tours);
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<String> ids) {
        favoriteIds.clear();
        if (ids != null) favoriteIds.addAll(ids);
        notifyDataSetChanged();
    }

    public void setListener(TourActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_destino, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Tour tour = data.get(position);
        holder.titulo.setText(tour.nombre);
        holder.ubicacion.setText(tour.destino);
        holder.precio.setText(String.format(Locale.getDefault(), "S/%.0f", tour.precio));
        holder.ratingTexto.setText(String.format(Locale.getDefault(), "%.1f • reseñas", tour.rating));
        holder.ratingEstrellas.setText("★★★★☆");
        holder.imagen.setImageResource(tour.imageRes);
        holder.btnDetalles.setOnClickListener(v -> {
            if (listener != null) listener.onDetalles(tour);
        });
        holder.favorito.setSelected(favoriteIds.contains(tour.id));
        holder.favorito.setOnClickListener(v -> {
            if (listener != null) listener.onFavorite(tour);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView imagen;
        final TextView titulo;
        final TextView ubicacion;
        final TextView precio;
        final TextView ratingEstrellas;
        final TextView ratingTexto;
        final Button btnDetalles;
        final ImageView favorito;

        VH(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.iv_destino_big);
            titulo = itemView.findViewById(R.id.tv_titulo);
            ubicacion = itemView.findViewById(R.id.tv_ubicacion);
            precio = itemView.findViewById(R.id.tv_precio_desde);
            ratingEstrellas = itemView.findViewById(R.id.tv_rating_estrellas);
            ratingTexto = itemView.findViewById(R.id.tv_rating_texto);
            btnDetalles = itemView.findViewById(R.id.btn_ver_detalles);
            favorito = itemView.findViewById(R.id.iv_favorito);
        }
    }
}
