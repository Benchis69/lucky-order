package com.example.luckyorder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.luckyorder.data.local.dao.*
import com.example.luckyorder.data.local.entity.*

@Database(
    entities = [
        User::class,
        RestaurantTable::class,
        Category::class,
        Product::class,
        Order::class,
        OrderItem::class
    ],
    version = 3,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    // Abstrakte Funktionen für jedes DAO. Room generiert den Code dafür automatisch.
    abstract fun userDao(): UserDao
    abstract fun tableDao(): TableDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        // @Volatile stellt sicher, dass Änderungen an INSTANCE sofort für alle Threads sichtbar sind
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton-Pattern: Wir wollen immer nur EINE Instanz der Datenbank im gesamten Projekt haben
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lucky_order_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}