package com.proyecto.travelia.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReviewEntity entity);

    @Query("SELECT * FROM reviews WHERE tourId = :tourId ORDER BY createdAt DESC")
    LiveData<List<ReviewEntity>> observeByTour(String tourId);

    @Query("SELECT AVG(rating) FROM reviews WHERE tourId = :tourId")
    LiveData<Double> observeAverage(String tourId);

    @Query("SELECT COUNT(*) FROM reviews WHERE tourId = :tourId")
    int countByTour(String tourId);
}
