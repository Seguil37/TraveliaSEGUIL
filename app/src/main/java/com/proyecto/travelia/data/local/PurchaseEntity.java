package com.proyecto.travelia.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchases")
public class PurchaseEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String orderId;
    public String userId;
    public String tourId;
    public String title;
    public String location;
    public String date;
    public String participants;
    public int participantsCount;
    public double unitPrice;
    public double totalPrice;
    public int imageRes;
    public long purchasedAt;

    public PurchaseEntity(@NonNull String id,
                          String orderId,
                          String userId,
                          String tourId,
                          String title,
                          String location,
                          String date,
                          String participants,
                          int participantsCount,
                          double unitPrice,
                          double totalPrice,
                          int imageRes,
                          long purchasedAt) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.tourId = tourId;
        this.title = title;
        this.location = location;
        this.date = date;
        this.participants = participants;
        this.participantsCount = participantsCount;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.imageRes = imageRes;
        this.purchasedAt = purchasedAt;
    }
}
