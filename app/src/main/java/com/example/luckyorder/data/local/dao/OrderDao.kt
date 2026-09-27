package com.example.luckyorder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.luckyorder.data.local.entity.Order
import com.example.luckyorder.data.local.entity.OrderItem
import com.example.luckyorder.data.local.entity.OrderStatus
import com.example.luckyorder.data.local.entity.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItem>)

    // @Transaction stellt sicher, dass Bestellung und Einzelpositionen zusammenhängend geladen werden
    @Transaction
    @Query("SELECT * FROM orders WHERE tableId = :tableId AND status = 'OPEN' LIMIT 1")
    fun getOpenOrderForTable(tableId: Long): Flow<OrderWithItems?>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderWithItemsById(orderId: Long): OrderWithItems?

    @Query("UPDATE orders SET status = :status, timestampClosed = :timestampClosed WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus, timestampClosed: Long? = System.currentTimeMillis())
}