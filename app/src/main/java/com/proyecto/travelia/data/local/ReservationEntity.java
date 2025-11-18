package com.proyecto.travelia.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reservations",
        indices = {@Index(value = {"userId"})})
public class ReservationEntity {

    @PrimaryKey
    @NonNull
    public String id; // combination of user/tour/date to avoid duplicates

    public String userId;
    public String tourId;
    public String title;
    public String location;
    public String date;
    public String participants;
    public double price;
    public int imageRes;
    public long createdAt;

    public ReservationEntity(@NonNull String id,
                             String userId,
                             String tourId,
                             String title,
                             String location,
                             String date,
                             String participants,
                             double price,
                             int imageRes,
                             long createdAt) {
        this.id = id;
        this.userId = userId;
        this.tourId = tourId;
        this.title = title;
        this.location = location;
        this.date = date;
        this.participants = participants;
        this.price = price;
        this.imageRes = imageRes;
        this.createdAt = createdAt;
    }
}
