package com.portfolio.notelensai.domain.usecase

import com.portfolio.notelensai.domain.repository.TextRecognitionRepository
import javax.inject.Inject

class ExtractTextUseCase @Inject constructor(
    private val repository: TextRecognitionRepository,
) {

    suspend operator fun invoke(imageUri: String): String {
        require(imageUri.isNotBlank()) {
            "Choose a note image first."
        }

        val extractedText = repository
            .extractText(imageUri)
            .trim()

        check(extractedText.isNotBlank()) {
            "No readable text was found. Try a clearer, well-lit photo."
        }

        return extractedText
    }
}