package com.example.luckyorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckyorder.data.local.AppDatabase
import com.example.luckyorder.data.local.entity.RestaurantTable
import com.example.luckyorder.data.repository.MenuRepository
import com.example.luckyorder.data.repository.OrderRepository
import com.example.luckyorder.ui.screens.TablePlanScreen
import com.example.luckyorder.ui.theme.LuckyOrderTheme
import com.example.luckyorder.ui.viewmodel.PosViewModel
import com.example.luckyorder.ui.viewmodel.PosViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize database from repos
        val database = AppDatabase.getDatabase(applicationContext)
        val menuRepository = MenuRepository(database.categoryDao(), database.productDao(), database.tableDao()
        )
        val orderRepository = OrderRepository(database.orderDao(), database.tableDao())

        // Testing
        lifecycleScope.launch {
            // Check if database is empty
            val currentTables = menuRepository.getAllTables().first()
            if (currentTables.isEmpty()) {
                for (i in 1..12) {
                    database.tableDao().insertTable(RestaurantTable(tableNumber = i, name = "Tisch $i"))
                }
            }
        }

        setContent {
            LuckyOrderTheme {
                // 2. Create ViewModel factory and call ViewModel
                val factory = remember { PosViewModelFactory(menuRepository, orderRepository) }
                val viewModel: PosViewModel = viewModel(factory = factory)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Call tableplan
                    TablePlanScreen(
                        viewModel = viewModel,
                        onTableClick = { clickedTable ->
                            // Navigate to order screen
                            viewModel.selectTable(clickedTable)
                            println("Tisch angetippt: ${clickedTable.name}")
                        }
                    )
                }
            }
        }
    }
}