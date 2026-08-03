package com.portfolio.notelensai.domain.usecase

import com.portfolio.notelensai.domain.repository.SummaryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GenerateSummaryUseCaseTest {

    @Test
    fun `passes cleaned notes to repository`() = runTest {
        val repository = RecordingSummaryRepository()
        val useCase = GenerateSummaryUseCase(repository)

        val result = useCase("  Kotlin is concise.  ")

        assertEquals("Kotlin is concise.", repository.receivedText)
        assertEquals("• Short summary", result)
    }

    @Test
    fun `rejects blank note text`() {
        val useCase = GenerateSummaryUseCase(RecordingSummaryRepository())

        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.test.runTest {
                useCase("   ")
            }
        }
    }

    private class RecordingSummaryRepository : SummaryRepository {
        var receivedText: String? = null

        override suspend fun summarize(noteText: String): String {
            receivedText = noteText
            return "• Short summary"
        }
    }
}

