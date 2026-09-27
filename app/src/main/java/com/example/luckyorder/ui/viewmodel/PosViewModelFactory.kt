package com.example.luckyorder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.luckyorder.data.repository.MenuRepository
import com.example.luckyorder.data.repository.OrderRepository

class PosViewModelFactory(private val menuRepository: MenuRepository, private val orderRepository: OrderRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosViewModel(menuRepository, orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}