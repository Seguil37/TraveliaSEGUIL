package com.proyecto.travelia.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.PurchaseDao;
import com.proyecto.travelia.data.local.PurchaseEntity;
import com.proyecto.travelia.data.local.ReservationEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PurchasesRepository {

    private final PurchaseDao dao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public PurchasesRepository(Context context) {
        dao = AppDatabase.get(context).purchaseDao();
    }

    public LiveData<List<PurchaseEntity>> observeOrder(String orderId) {
        return dao.observeByOrder(orderId);
    }

    public LiveData<List<PurchaseEntity>> observeUser(String userId) {
        return dao.observeByUser(userId);
    }

    public void recordPurchase(String orderId, String userId, List<ReservationEntity> reservations) {
        List<PurchaseEntity> list = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        for (ReservationEntity entity : reservations) {
            String id = orderId + "_" + entity.id;
            list.add(new PurchaseEntity(id,
                    orderId,
                    userId,
                    entity.tourId,
                    entity.title,
                    entity.location,
                    entity.date,
                    entity.participants,
                    entity.participantsCount,
                    entity.unitPrice,
                    entity.price,
                    entity.imageRes,
                    timestamp));
        }
        ioExecutor.execute(() -> dao.insertAll(list));
    }

    public static String buildOrderId() {
        return "ORD-" + String.format(Locale.getDefault(), "%d", System.currentTimeMillis());
    }
}
