package com.portfolio.notelensai.domain.repository

interface SummaryRepository {
    suspend fun summarize(noteText: String): String
}

