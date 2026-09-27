package com.example.luckyorder.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckyorder.data.local.entity.RestaurantTable
import com.example.luckyorder.ui.viewmodel.PosViewModel


@OptIn(ExperimentalSubclassOptIn::class, ExperimentalMaterial3Api::class)
@Composable
fun TablePlanScreen (viewModel: PosViewModel, onTableClick: (RestaurantTable) -> Unit) {
    // Collects the tables from the ViewModel. If the DB changes, the ui will be updated automatically
    val tables by viewModel.tables.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title= { Text("Tischplan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            // Adaptive size: On tablet more columns will be displayed
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            items(tables) { table ->
                TableItem(table = table, onClick = { onTableClick(table) })
            }
        }
    }
}

@Composable
fun TableItem(table: RestaurantTable, onClick: () -> Unit) {
    // Free tables are green, occupied ones are red
    val backgroundColor = if (table.isOccupied) Color(0xFFE57373) else Color(0xFF81C784)

    Card(
        modifier = Modifier.aspectRatio(1f).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = table.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}