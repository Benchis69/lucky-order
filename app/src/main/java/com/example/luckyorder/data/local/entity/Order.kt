package com.example.luckyorder.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

enum class OrderStatus { OPEN, PAID, CANCELLED }

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(entity = RestaurantTable::class, parentColumns = ["tableId"], childColumns = ["tableId"]),
        ForeignKey(entity = User::class, parentColumns = ["userId"], childColumns = ["userId"])
    ]
)

data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0,
    val tableId: Long,
    val userId: Long,
    val timestampOpen: Long,
    val timestampClosed: Long? = null,
    val status: OrderStatus
)

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(entity = Order::class, parentColumns = ["orderId"], childColumns = ["orderId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Product::class, parentColumns = ["productId"], childColumns = ["productId"])
    ]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val orderItemId: Long = 0,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val priceAtTimeInCents: Int,
    val taxRateAtTime: Int,
    val notes: String? = null,
    val isPrintedToKitchen: Boolean = false
)

data class OrderWithItems(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItem>
)