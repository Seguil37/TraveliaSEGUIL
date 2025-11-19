package com.proyecto.travelia.reviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.R;
import com.proyecto.travelia.data.local.ReviewEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.VH> {

    private final List<ReviewEntity> data = new ArrayList<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public void submit(List<ReviewEntity> reviews) {
        data.clear();
        if (reviews != null) {
            data.addAll(reviews);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ReviewEntity entity = data.get(position);
        holder.user.setText(entity.userName != null ? entity.userName : "Viajero");
        holder.comment.setText(entity.comment != null ? entity.comment : "");
        holder.rating.setRating(entity.rating);
        holder.date.setText(formatter.format(new Date(entity.createdAt)));
        holder.avatar.setText(firstLetter(entity.userName));
    }

    private String firstLetter(String name) {
        if (name == null || name.isEmpty()) return "?";
        return name.substring(0, 1).toUpperCase(Locale.getDefault());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView avatar;
        final TextView user;
        final RatingBar rating;
        final TextView comment;
        final TextView date;

        VH(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.tv_avatar);
            user = itemView.findViewById(R.id.tv_review_user);
            rating = itemView.findViewById(R.id.rb_review);
            comment = itemView.findViewById(R.id.tv_review_comment);
            date = itemView.findViewById(R.id.tv_review_date);
        }
    }
}
