package com.proyecto.travelia.compras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.travelia.R;
import com.proyecto.travelia.data.local.PurchaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComprasAdapter extends RecyclerView.Adapter<ComprasAdapter.VH> {

    private final List<PurchaseEntity> data = new ArrayList<>();

    public void submit(List<PurchaseEntity> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compra_resumen, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PurchaseEntity entity = data.get(position);
        holder.titulo.setText(entity.title);
        String location = entity.location != null ? entity.location : "-";
        String fecha = entity.date != null ? entity.date : "-";
        String participantes = entity.participants != null ? entity.participants : "-";
        String detalle = location + " • " + fecha + " • " + participantes;
        holder.detalle.setText(detalle);
        holder.total.setText(String.format(Locale.getDefault(), "Total: S/%.2f", entity.totalPrice));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView titulo;
        final TextView detalle;
        final TextView total;

        VH(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_compra_titulo);
            detalle = itemView.findViewById(R.id.tv_compra_detalle);
            total = itemView.findViewById(R.id.tv_compra_total);
        }
    }
}
