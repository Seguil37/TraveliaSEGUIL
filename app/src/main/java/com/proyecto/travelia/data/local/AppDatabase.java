package com.proyecto.travelia.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {
        FavoriteEntity.class,
        ReservationEntity.class,
        UserEntity.class,
        PurchaseEntity.class
}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoritesDao favoritesDao();
    public abstract ReservationDao reservationDao();
    public abstract UserDao userDao();
    public abstract PurchaseDao purchaseDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    AppDatabase.class,
                                    "travelia.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
