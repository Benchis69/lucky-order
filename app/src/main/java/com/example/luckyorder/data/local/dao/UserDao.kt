package com.example.luckyorder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.luckyorder.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE pinHash = :pinHash LIMIT 1")
    suspend fun getUserByPin(pinHash: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}