package com.example.luckyorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.luckyorder.data.local.AppDatabase
import com.example.luckyorder.data.local.entity.RestaurantTable
import com.example.luckyorder.data.repository.MenuRepository
import com.example.luckyorder.data.repository.OrderRepository
import com.example.luckyorder.databinding.ActivityMainBinding
import com.example.luckyorder.ui.TableAdapter
import com.example.luckyorder.ui.viewmodel.PosViewModel
import com.example.luckyorder.ui.viewmodel.PosViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. XML Layout binden
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Datenbank und Repositories initialisieren
        val database = AppDatabase.getDatabase(applicationContext)
        val menuRepository = MenuRepository(database.categoryDao(), database.productDao(), database.tableDao())
        val orderRepository = OrderRepository(database.orderDao(), database.tableDao())

        // 3. ViewModel laden
        val factory = PosViewModelFactory(menuRepository, orderRepository)
        viewModel = ViewModelProvider(this, factory)[PosViewModel::class.java]

        // 4. Dummy-Daten beim Start laden
        lifecycleScope.launch {
            val currentTables = menuRepository.getAllTables().first()
            if (currentTables.isEmpty()) {
                for (i in 1..12) {
                    database.tableDao().insertTable(RestaurantTable(tableNumber = i, name = "Tisch $i"))
                }
            }
        }

        // 5. RecyclerView (Tischplan) einrichten
        val tableAdapter = TableAdapter { clickedTable ->
            viewModel.selectTable(clickedTable)
            println("Tisch angetippt: ${clickedTable.name}")
        }

        // Raster mit 3 Spalten (lässt sich später dynamisch für Tablets anpassen)
        binding.recyclerViewTables.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewTables.adapter = tableAdapter

        // 6. Daten aus dem ViewModel beobachten und an den Adapter senden
        lifecycleScope.launch {
            viewModel.tables.collect { tablesList ->
                tableAdapter.submitList(tablesList)
            }
        }
    }
}