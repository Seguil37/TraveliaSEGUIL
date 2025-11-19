package com.proyecto.travelia.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String name;
    public String email;
    public String phone;
    public String nationality;
    public String passwordHash;
    public boolean notificationsEnabled;
    public long createdAt;
    public long updatedAt;

    public UserEntity(@NonNull String id,
                      String name,
                      String email,
                      String phone,
                      String nationality,
                      String passwordHash,
                      boolean notificationsEnabled,
                      long createdAt,
                      long updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nationality = nationality;
        this.passwordHash = passwordHash;
        this.notificationsEnabled = notificationsEnabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
