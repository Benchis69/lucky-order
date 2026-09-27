package com.example.luckyorder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.luckyorder.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1")
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isActive = 1")
    fun getAllActiveProducts(): Flow<List<Product>>
}