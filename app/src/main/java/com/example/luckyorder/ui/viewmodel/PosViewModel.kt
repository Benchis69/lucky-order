package com.example.luckyorder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luckyorder.data.local.entity.Category
import com.example.luckyorder.data.local.entity.OrderWithItems
import com.example.luckyorder.data.local.entity.Product
import com.example.luckyorder.data.local.entity.RestaurantTable
import com.example.luckyorder.data.repository.MenuRepository
import com.example.luckyorder.data.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PosViewModel(private val menuRepository: MenuRepository, private val orderRepository: OrderRepository) : ViewModel() {

    // 1. UI State for table selection (live update if table is occupied)
    val tables: StateFlow<List<RestaurantTable>> = menuRepository.getAllTables().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedTable = MutableStateFlow<RestaurantTable?> (null)
    val selectedTable: StateFlow<RestaurantTable?> = _selectedTable.asStateFlow()

    // 2. UI State for the active order of the selected Table
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentOrder: StateFlow<OrderWithItems?> = _selectedTable.flatMapLatest { table ->
        if (table != null) {
            orderRepository.getOpenOrderForTable(table.tableId)
        }
        else {
            flowOf(null)
        }
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // 3. UI State for the menu (categories & products)
    val categories: StateFlow<List<Category>> = menuRepository.getAllCategories().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedCategory = MutableStateFlow<Category?> (null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val productsForCategory: StateFlow<List<Product>?> = _selectedCategory.flatMapLatest { category ->
        if (category != null) {
            menuRepository.getProductsByCategory(category.categoryId)
        }
        else {
            flowOf(null)
        }
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- User actions (events, called by the UI) ---

    fun selectTable(table: RestaurantTable) {
        _selectedTable.value = table
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun addProductToOrder(product: Product, userId: Long = 1L) {
        // userId stays hardcoded to 1 for now (login screen later)
        val table = _selectedTable.value ?: return
        viewModelScope.launch {
            orderRepository.addProductToOrder(table.tableId, userId, product)
        }
    }

    fun checkoutCurrentTable() {
        val order = currentOrder.value?.order ?: return
        viewModelScope.launch {
            orderRepository.checkoutOrder(order.orderId, order.tableId)
            _selectedTable.value = null // Nach dem Bezahlen den Tisch wieder abwählen
        }
    }
}