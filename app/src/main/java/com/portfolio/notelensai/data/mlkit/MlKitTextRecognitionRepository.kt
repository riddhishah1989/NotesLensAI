package com.portfolio.notelensai.data.mlkit

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.portfolio.notelensai.domain.repository.TextRecognitionRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class MlKitTextRecognitionRepository(
    private val context: Context,
) : TextRecognitionRepository {

    override suspend fun extractText(imageUri: String): String =
        suspendCancellableCoroutine { continuation ->
            val inputImage = runCatching {
                InputImage.fromFilePath(context, Uri.parse(imageUri))
            }.getOrElse { error ->
                continuation.resumeWithException(error)
                return@suspendCancellableCoroutine
            }

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            continuation.invokeOnCancellation { recognizer.close() }

            recognizer.process(inputImage)
                .addOnSuccessListener { result ->
                    if (continuation.isActive) {
                        continuation.resume(result.text)
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(error)
                    }
                }
                .addOnCompleteListener {
                    recognizer.close()
                }
        }
}

