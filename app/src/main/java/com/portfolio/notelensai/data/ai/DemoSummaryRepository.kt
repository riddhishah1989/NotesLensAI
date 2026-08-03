package com.portfolio.notelensai.data.ai

import com.portfolio.notelensai.domain.repository.SummaryRepository
import kotlinx.coroutines.delay

class DemoSummaryRepository : SummaryRepository {

    override suspend fun summarize(noteText: String): String {
        delay(350)

        val normalized = noteText.replace(Regex("\\s+"), " ").trim()
        val sentences = normalized
            .split(Regex("(?<=[.!?])\\s+"))
            .map(String::trim)
            .filter(String::isNotBlank)

        val points = when {
            sentences.size >= 2 -> sentences.take(3)
            normalized.length > 220 -> normalized
                .chunked(180)
                .take(3)
                .map { it.trim() }
            else -> listOf(normalized)
        }

        return points.joinToString(separator = "\n") { point ->
            "• ${point.take(220).trim()}"
        }
    }
}

