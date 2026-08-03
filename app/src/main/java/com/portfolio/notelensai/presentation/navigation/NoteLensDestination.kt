package com.portfolio.notelensai.presentation.navigation

sealed class Screen(val route: String) {
    data object SelectNote : Screen("select_note")
    data object ExtractedText : Screen("extracted_text")
    data object Summary : Screen("summary")
}

internal const val NOTE_LENS_GRAPH_ROUTE = "note_lens_graph"
