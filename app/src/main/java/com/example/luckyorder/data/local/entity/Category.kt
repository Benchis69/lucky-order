package com.example.luckyorder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    val name: String,
    val taxRateInhouse: Int,
    val taxRateTakeaway: Int
)