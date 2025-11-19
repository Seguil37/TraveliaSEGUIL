package com.proyecto.travelia.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PurchaseEntity> entities);

    @Query("SELECT * FROM purchases WHERE userId = :userId ORDER BY purchasedAt DESC")
    LiveData<List<PurchaseEntity>> observeByUser(String userId);

    @Query("SELECT * FROM purchases WHERE orderId = :orderId ORDER BY purchasedAt DESC")
    LiveData<List<PurchaseEntity>> observeByOrder(String orderId);
}
