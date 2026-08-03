package com.portfolio.notelensai.domain.repository

interface TextRecognitionRepository {
    suspend fun extractText(imageUri: String): String
}

