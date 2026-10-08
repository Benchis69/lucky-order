package com.example.luckyorder

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.luckyorder.data.local.AppDatabase
import com.example.luckyorder.data.local.entity.Category
import com.example.luckyorder.data.local.entity.RestaurantTable
import com.example.luckyorder.data.repository.MenuRepository
import com.example.luckyorder.data.repository.OrderRepository
import com.example.luckyorder.databinding.ActivityMainBinding
import com.example.luckyorder.ui.CategoryAdapter
import com.example.luckyorder.ui.TableAdapter
import com.example.luckyorder.ui.viewmodel.PosViewModel
import com.example.luckyorder.ui.viewmodel.PosViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

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
        // Prüfen, ob das Gerät ein Tablet (sw600dp) oder ein Smartphone ist
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600
        val spanCountTables = if (isTablet) 4 else 2 // 4 Spalten auf Tablets, 2 Spalten auf Smartphones

        binding.recyclerViewTables.layoutManager = GridLayoutManager(this, spanCountTables)
        binding.recyclerViewTables.adapter = tableAdapter

        // 6. Daten aus dem ViewModel beobachten und an den Adapter senden
        lifecycleScope.launch {
            viewModel.tables.collect { tablesList ->
                tableAdapter.submitList(tablesList)
            }
        }
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnTischplan -> {
                        // Zeige den Tischplan, verstecke die Speisekarte
                        binding.recyclerViewTables.visibility = android.view.View.VISIBLE
                        binding.layoutSpeisekarte.visibility = android.view.View.GONE

                        // Optional: Die Raum-Tabs wieder einblenden (Speisekarte braucht die evtl. nicht)
                        binding.tabLayoutAreas.visibility = android.view.View.VISIBLE
                    }
                    R.id.btnSpeisekarte -> {
                        // Zeige die Speisekarte, verstecke den Tischplan
                        binding.recyclerViewTables.visibility = android.view.View.GONE
                        binding.layoutSpeisekarte.visibility = android.view.View.VISIBLE

                        // Optional: Raum-Tabs auf der Speisekarte ausblenden
                        binding.tabLayoutAreas.visibility = android.view.View.GONE
                    }
                }
            }
        }

        binding.btnOpenSidebar?.setOnClickListener {
            (binding.drawerLayout as? DrawerLayout)?.openDrawer(GravityCompat.START)
        }

        val recyclerView = binding.rvCategories
        val spanCountCategories = if (isTablet) 4 else 3

        recyclerView.layoutManager = GridLayoutManager(this, spanCountCategories)

        val categoryAdapter = CategoryAdapter(emptyList()) { selectedCategory ->
            // Klick-Aktion: Hier laden wir gleich die Produkte der Kategorie
            Toast.makeText(this@MainActivity, "Kategorie: ${selectedCategory.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = categoryAdapter

        val dummyCategories = listOf(
            Category(1, "Vorspeisen", "#B2EBF2", "Speisen", 19, 19),
            Category(2, "Salate &\nVegetarisch", "#A5D6A7", "Speisen", 19, 19),
            Category(3, "Fisch", "#90CAF9", "Speisen", 19, 19)
        )

        categoryAdapter.updateData(dummyCategories)
    }
}