package com.proyecto.travelia.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findByEmailSync(String email);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity findByIdSync(long id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<UserEntity> observeById(long id);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(UserEntity entity);

    @Update
    void update(UserEntity entity);
}
