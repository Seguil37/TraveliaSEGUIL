package com.proyecto.travelia.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String email;
    public String password;
    public String phone;
    public long createdAt;
    public long updatedAt;

    public UserEntity(String name,
                      String email,
                      String password,
                      String phone,
                      long createdAt,
                      long updatedAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
