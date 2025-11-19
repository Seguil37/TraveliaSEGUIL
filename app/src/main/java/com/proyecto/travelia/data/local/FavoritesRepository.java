package com.proyecto.travelia.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.FavoriteEntity;
import com.proyecto.travelia.data.local.FavoritesDao;
import com.proyecto.travelia.data.session.UserSessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesRepository {

    private final FavoritesDao dao;
    private final UserSessionManager sessionManager;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    public FavoritesRepository(Context ctx, UserSessionManager sessionManager) {
        this.dao = AppDatabase.get(ctx).favoritesDao();
        this.sessionManager = sessionManager;
    }

    private String userId() {
        return sessionManager.getActiveOrGuestId();
    }

    public boolean isFavoriteSync(String itemId) {
        return dao.existsSync(userId(), itemId, "TOUR") > 0;
    }

    public void add(FavoriteEntity e) {
        e.userId = userId();
        ioExecutor.execute(() -> dao.insert(e));
    }

    public void remove(String itemId) {
        ioExecutor.execute(() -> dao.deleteByKey(userId(), itemId, "TOUR"));
    }

    public LiveData<List<FavoriteEntity>> observeAll() {
        return dao.observeAll(userId());
    }

    public LiveData<Integer> observeCount() {
        return dao.observeCount(userId());
    }
}
