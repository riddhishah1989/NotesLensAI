package com.portfolio.notelensai.presentation

import com.portfolio.notelensai.domain.repository.SummaryRepository
import com.portfolio.notelensai.domain.repository.TextRecognitionRepository
import com.portfolio.notelensai.domain.usecase.ExtractTextUseCase
import com.portfolio.notelensai.domain.usecase.GenerateSummaryUseCase
import com.portfolio.notelensai.presentation.screen.NoteLensViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NoteLensViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `extract text updates screen state`() {
        val viewModel = createViewModel(extractedText = "Recognized note")
        viewModel.onImageSelected("content://note")

        viewModel.extractText()

        assertEquals("Recognized note", viewModel.uiState.value.extractedText)
        assertFalse(viewModel.uiState.value.isExtracting)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `generate summary updates screen state`() {
        val viewModel = createViewModel(summary = "• Concise summary")
        viewModel.onExtractedTextChanged("A longer note")

        viewModel.generateSummary()

        assertEquals("• Concise summary", viewModel.uiState.value.summary)
        assertFalse(viewModel.uiState.value.isSummarizing)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `extract without selected image shows a useful error`() {
        val viewModel = createViewModel()

        viewModel.extractText()

        assertEquals(
            "Take a photo or choose one from the gallery first.",
            viewModel.uiState.value.errorMessage,
        )
    }

    private fun createViewModel(
        extractedText: String = "Text",
        summary: String = "• Summary",
    ): NoteLensViewModel {
        val textRepository = object : TextRecognitionRepository {
            override suspend fun extractText(imageUri: String): String = extractedText
        }
        val summaryRepository = object : SummaryRepository {
            override suspend fun summarize(noteText: String): String = summary
        }
        return NoteLensViewModel(
            extractTextUseCase = ExtractTextUseCase(textRepository),
            generateSummaryUseCase = GenerateSummaryUseCase(summaryRepository),
            isDemoMode = false,
        )
    }
}

