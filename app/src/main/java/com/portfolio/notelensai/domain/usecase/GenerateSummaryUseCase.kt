package com.portfolio.notelensai.domain.usecase

import com.portfolio.notelensai.domain.repository.SummaryRepository
import javax.inject.Inject

class GenerateSummaryUseCase @Inject constructor(
    private val repository: SummaryRepository,
) {

    suspend operator fun invoke(noteText: String): String {
        val cleanedText = noteText.trim()

        require(cleanedText.isNotBlank()) {
            "Add some note text before summarizing."
        }

        val boundedText = cleanedText.take(MAX_NOTE_CHARACTERS)
        val summary = repository.summarize(boundedText).trim()

        check(summary.isNotBlank()) {
            "Gemini returned an empty summary. Please try again."
        }

        return summary
    }

    private companion object {
        const val MAX_NOTE_CHARACTERS = 12_000
    }
}