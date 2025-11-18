package com.proyecto.travelia.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.FavoriteEntity;
import com.proyecto.travelia.data.local.FavoritesDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesRepository {

    private final FavoritesDao dao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public FavoritesRepository(Context ctx) {
        this.dao = AppDatabase.get(ctx).favoritesDao();
    }

    public boolean isFavoriteSync(String userId, String itemId) {
        if (userId == null) return false;
        return dao.existsSync(userId, itemId, "TOUR") > 0;
    }

    public void add(FavoriteEntity e) {
        ioExecutor.execute(() -> dao.insert(e));
    }

    public void remove(String userId, String itemId) {
        if (userId == null) return;
        ioExecutor.execute(() -> dao.deleteByKey(userId, itemId, "TOUR"));
    }

    public LiveData<List<FavoriteEntity>> observeByUser(String userId) {
        return dao.observeByUser(userId);
    }
}
