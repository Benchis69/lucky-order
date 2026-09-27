package com.example.luckyorder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Role { ADMIN, WAITER }

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId : Long = 0,
    val name: String,
    val pinHash: String,
    val role: Role
)
