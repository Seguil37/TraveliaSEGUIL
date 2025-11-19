package com.proyecto.travelia.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReservationDao {

    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<ReservationEntity>> observeAll(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(ReservationEntity entity);

    @Query("DELETE FROM reservations WHERE id = :reservationId AND userId = :userId")
    void deleteById(String reservationId, String userId);

    @Query("DELETE FROM reservations WHERE userId = :userId")
    void clearAll(String userId);
}
