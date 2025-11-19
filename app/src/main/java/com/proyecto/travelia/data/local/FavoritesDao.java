package com.proyecto.travelia.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoritesDao {

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY updatedAt DESC")
    LiveData<List<FavoriteEntity>> observeAll(String userId);

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    LiveData<Integer> observeCount(String userId);

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND itemId = :itemId AND itemType = :itemType")
    int existsSync(String userId, String itemId, String itemType);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(FavoriteEntity entity);

    @Query("DELETE FROM favorites WHERE userId = :userId AND itemId = :itemId AND itemType = :itemType")
    void deleteByKey(String userId, String itemId, String itemType);
}
