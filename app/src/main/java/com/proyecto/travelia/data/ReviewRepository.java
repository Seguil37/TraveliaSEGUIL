package com.proyecto.travelia.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.ReviewDao;
import com.proyecto.travelia.data.local.ReviewEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewRepository {

    private final ReviewDao dao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public ReviewRepository(Context context) {
        dao = AppDatabase.get(context).reviewDao();
    }

    public LiveData<List<ReviewEntity>> observe(String tourId) {
        return dao.observeByTour(tourId);
    }

    public LiveData<Double> observeAverage(String tourId) {
        return dao.observeAverage(tourId);
    }

    public void addReview(String tourId,
                          String userId,
                          String userName,
                          float rating,
                          String comment) {
        ReviewEntity entity = new ReviewEntity(tourId, userId, userName, rating, comment, System.currentTimeMillis());
        ioExecutor.execute(() -> dao.insert(entity));
    }

    public void seedIfNeeded(String tourId) {
        ioExecutor.execute(() -> {
            if (dao.countByTour(tourId) > 0) return;
            dao.insert(new ReviewEntity(tourId, "demo", "Mariana", 4.5f,
                    "Una experiencia inolvidable, guías súper atentos.", System.currentTimeMillis() - 86400000L));
            dao.insert(new ReviewEntity(tourId, "demo2", "Luis", 4.0f,
                    "Buen servicio y logística, volvería a viajar con Travelia.", System.currentTimeMillis() - 43200000L));
        });
    }
}
