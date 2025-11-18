package com.proyecto.travelia.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.ReservationDao;
import com.proyecto.travelia.data.local.ReservationEntity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationsRepository {

    private final ReservationDao dao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public ReservationsRepository(Context context) {
        dao = AppDatabase.get(context).reservationDao();
    }

    public LiveData<List<ReservationEntity>> observeByUser(String userId) {
        return dao.observeByUser(userId);
    }

    public void upsert(ReservationEntity entity) {
        ioExecutor.execute(() -> dao.upsert(entity));
    }

    public void remove(String reservationId, String userId) {
        if (userId == null) return;
        ioExecutor.execute(() -> dao.deleteById(reservationId, userId));
    }

    public void clearAll(String userId) {
        if (userId == null) return;
        ioExecutor.execute(() -> dao.clearAll(userId));
    }

    public static String buildId(String userId, String tourId, String date) {
        String safeUser = userId == null ? "guest" : userId.trim();
        String safeTour = tourId == null ? "tour" : tourId.trim();
        String safeDate = date == null ?
                String.format(Locale.getDefault(), "%d", System.currentTimeMillis()) :
                date.trim();
        return safeUser + "_" + safeTour + "_" + safeDate.replaceAll("\\s", "_");
    }
}
