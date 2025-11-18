package com.proyecto.travelia.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.PurchaseDao;
import com.proyecto.travelia.data.local.PurchaseEntity;
import com.proyecto.travelia.data.local.ReservationEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PurchaseRepository {

    private final PurchaseDao purchaseDao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public PurchaseRepository(Context context) {
        purchaseDao = AppDatabase.get(context).purchaseDao();
    }

    public LiveData<List<PurchaseEntity>> observeByUser(String userId) {
        return purchaseDao.observeByUser(userId);
    }

    public void saveAll(String userId, List<ReservationEntity> reservations) {
        if (reservations == null || reservations.isEmpty()) return;
        ioExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            for (ReservationEntity reservation : reservations) {
                PurchaseEntity entity = new PurchaseEntity(
                        userId,
                        reservation.id,
                        reservation.tourId,
                        reservation.title,
                        reservation.location,
                        reservation.date,
                        reservation.participants,
                        reservation.price,
                        reservation.imageRes,
                        now
                );
                purchaseDao.insert(entity);
            }
        });
    }

    public void clearForUser(String userId) {
        ioExecutor.execute(() -> purchaseDao.clearForUser(userId));
    }
}
