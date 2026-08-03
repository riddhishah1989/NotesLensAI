package com.portfolio.notelensai.presentation.navigation

import androidx.navigation.NavHostController

fun NavHostController.navigateToExtractedText() {
    navigate(Screen.ExtractedText.route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToSummary() {
    navigate(Screen.Summary.route) {
        launchSingleTop = true
    }
}

fun NavHostController.startNewScan() {
    navigate(Screen.SelectNote.route) {
        popUpTo(Screen.SelectNote.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
