package com.portfolio.notelensai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.portfolio.notelensai.presentation.screen.ExtractedTextRoute
import com.portfolio.notelensai.presentation.screen.NoteInputRoute
import com.portfolio.notelensai.presentation.screen.SummaryRoute

@Composable
fun NoteLensNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.SelectNote.route,
) {
    NavHost(
        navController = navController,
        startDestination = NOTE_LENS_GRAPH_ROUTE,
    ) {
        navigation(
            route = NOTE_LENS_GRAPH_ROUTE,
            startDestination = startDestination,
        ) {
            composable(Screen.SelectNote.route) { backStackEntry ->
                val graphOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(NOTE_LENS_GRAPH_ROUTE)
                }

                NoteInputRoute(
                    viewModelStoreOwner = graphOwner,
                    onTextExtracted = navController::navigateToExtractedText,
                )
            }

            composable(Screen.ExtractedText.route) { backStackEntry ->
                val graphOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(NOTE_LENS_GRAPH_ROUTE)
                }

                ExtractedTextRoute(
                    viewModelStoreOwner = graphOwner,
                    onSummaryGenerated = navController::navigateToSummary,
                    onBack = { navController.navigateUp() },
                )
            }

            composable(Screen.Summary.route) { backStackEntry ->
                val graphOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(NOTE_LENS_GRAPH_ROUTE)
                }

                SummaryRoute(
                    viewModelStoreOwner = graphOwner,
                    onStartOver = navController::startNewScan,
                    onBack = { navController.navigateUp() },
                )
            }
        }
    }
}
