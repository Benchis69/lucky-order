package com.example.luckyorder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_tables")
data class RestaurantTable(
    @PrimaryKey(autoGenerate = true) val tableId: Long = 0,
    val tableNumber: Int,
    val name: String,
    var sumPriceInCents: Int = 0,
    val isOccupied: Boolean = false
)