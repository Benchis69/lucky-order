package com.example.luckyorder.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val productId: Long = 0,
    val categoryId: Long,
    val name: String,
    val priceInCents: Int,
    val isActive: Boolean = true
)