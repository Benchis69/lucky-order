package com.example.luckyorder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.luckyorder.data.local.entity.RestaurantTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: RestaurantTable): Long

    @Query("SELECT * FROM restaurant_tables ORDER BY tableNumber ASC")
    fun getAllTables(): Flow<List<RestaurantTable>>

    @Query("UPDATE restaurant_tables SET isOccupied = :isOccupied WHERE tableId = :tableId")
    suspend fun updateTableOccupancy(tableId: Long, isOccupied: Boolean)
}