package com.portfolio.notelensai.domain.usecase

import com.portfolio.notelensai.domain.repository.TextRecognitionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ExtractTextUseCaseTest {

    @Test
    fun `returns trimmed recognized text`() = runTest {
        val useCase = ExtractTextUseCase(
            repository = FakeTextRecognitionRepository("  Readable note text  "),
        )

        val result = useCase("content://note")

        assertEquals("Readable note text", result)
    }

    @Test
    fun `rejects an empty recognition result`() {
        val useCase = ExtractTextUseCase(
            repository = FakeTextRecognitionRepository("   "),
        )

        assertThrows(IllegalStateException::class.java) {
            kotlinx.coroutines.test.runTest {
                useCase("content://note")
            }
        }
    }

    private class FakeTextRecognitionRepository(
        private val result: String,
    ) : TextRecognitionRepository {
        override suspend fun extractText(imageUri: String): String = result
    }
}

