package com.proyecto.travelia.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases", indices = {@Index(value = {"userId"})})
public class PurchaseEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String userId;
    public String reservationId;
    public String tourId;
    public String title;
    public String location;
    public String date;
    public String participants;
    public double price;
    public int imageRes;
    public long purchasedAt;

    public PurchaseEntity(String userId,
                          String reservationId,
                          String tourId,
                          String title,
                          String location,
                          String date,
                          String participants,
                          double price,
                          int imageRes,
                          long purchasedAt) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.tourId = tourId;
        this.title = title;
        this.location = location;
        this.date = date;
        this.participants = participants;
        this.price = price;
        this.imageRes = imageRes;
        this.purchasedAt = purchasedAt;
    }
}
