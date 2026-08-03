package com.portfolio.notelensai.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.portfolio.notelensai.presentation.navigation.NoteLensNavGraph

@Composable
fun NoteLensApp() {
    val navController = rememberNavController()

    NoteLensNavGraph(navController = navController)
}
