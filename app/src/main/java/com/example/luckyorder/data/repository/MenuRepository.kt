package com.example.luckyorder.data.repository

import com.example.luckyorder.data.local.dao.CategoryDao
import com.example.luckyorder.data.local.dao.ProductDao
import com.example.luckyorder.data.local.dao.TableDao

class MenuRepository (private val categoryDao: CategoryDao, private val productDao: ProductDao, private val tableDao: TableDao) {

    fun getAllTables() = tableDao.getAllTables()

    fun getAllCategories() = categoryDao.getAllCategories()

    fun getProductsByCategory(categoryId: Long) = productDao.getProductsByCategory(categoryId)
}