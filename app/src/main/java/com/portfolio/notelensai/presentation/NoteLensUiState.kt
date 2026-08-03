package com.portfolio.notelensai.presentation

data class NoteLensUiState(
    val selectedImageUri: String? = null,
    val extractedText: String = "",
    val summary: String = "",
    val isExtracting: Boolean = false,
    val isSummarizing: Boolean = false,
    val errorMessage: String? = null,
    val isDemoMode: Boolean = false,
)

sealed interface NoteLensEffect {
    data object TextExtractionCompleted : NoteLensEffect
    data object SummaryGenerationCompleted : NoteLensEffect
}
