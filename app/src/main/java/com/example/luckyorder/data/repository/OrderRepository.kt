package com.example.luckyorder.data.repository

import com.example.luckyorder.data.local.dao.OrderDao
import com.example.luckyorder.data.local.dao.TableDao
import com.example.luckyorder.data.local.entity.Order
import com.example.luckyorder.data.local.entity.OrderItem
import com.example.luckyorder.data.local.entity.OrderStatus
import com.example.luckyorder.data.local.entity.Product
import kotlinx.coroutines.flow.firstOrNull

class OrderRepository(private val orderDao: OrderDao, private val tableDao: TableDao) {

    fun getOpenOrderForTable(tableId: Long) = orderDao.getOpenOrderForTable(tableId)

    suspend fun addProductToOrder(tableId: Long, userId: Long, product: Product) {
        // 1. is there a running order?
        val currentOrderWithItems = orderDao.getOpenOrderForTable(tableId).firstOrNull()

        val orderId = if (currentOrderWithItems == null) {
            // 2a. No new order -> create a new one
            val newOrder = Order(
                tableId = tableId,
                userId = userId,
                timestampOpen = System.currentTimeMillis(),
                status = OrderStatus.OPEN
            )
            val newOrderId = orderDao.insertOrder(newOrder)

            // Set table visually to occupied
            tableDao.updateTableOccupancy(tableId, isOccupied = true)

            newOrderId
        }
        else {
            // 2b. Table is already open -> use existing ID
            currentOrderWithItems.order.orderId
        }

        // 3. Add selected product to order
        val orderItem = OrderItem(
            orderId = orderId,
            productId = product.productId,
            quantity = 1,
            priceAtTimeInCents = product.priceInCents,
            taxRateAtTime = 19, // musst be computed dynamically based on category later
            isPrintedToKitchen = false
        )
        orderDao.insertOrderItem(orderItem)
    }

    suspend fun checkoutOrder(orderId: Long, tableId: Long) {
        // 1. Change status of the order to "PAYED" and change timestamp
        orderDao.updateOrderStatus(orderId, OrderStatus.PAID)

        // 2. Mark table as free again
        tableDao.updateTableOccupancy(tableId, isOccupied = false)
    }
}