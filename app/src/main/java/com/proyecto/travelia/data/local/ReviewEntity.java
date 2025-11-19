package com.proyecto.travelia.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews")
public class ReviewEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String tourId;

    public String userId;
    public String userName;
    public float rating;
    public String comment;
    public long createdAt;

    public ReviewEntity(@NonNull String tourId,
                        String userId,
                        String userName,
                        float rating,
                        String comment,
                        long createdAt) {
        this.tourId = tourId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
