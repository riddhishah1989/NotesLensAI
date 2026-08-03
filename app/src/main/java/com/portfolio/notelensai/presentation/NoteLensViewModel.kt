package com.portfolio.notelensai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.notelensai.di.DemoMode
import com.portfolio.notelensai.domain.usecase.ExtractTextUseCase
import com.portfolio.notelensai.domain.usecase.GenerateSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NoteLensViewModel @Inject constructor(
    private val extractTextUseCase: ExtractTextUseCase,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    @DemoMode isDemoMode: Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NoteLensUiState(isDemoMode = isDemoMode),
    )
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<NoteLensEffect>(
        extraBufferCapacity = 1,
    )
    val effects = _effects.asSharedFlow()

    fun onImageSelected(imageUri: String) {
        _uiState.update { state ->
            state.copy(
                selectedImageUri = imageUri,
                extractedText = "",
                summary = "",
                errorMessage = null,
            )
        }
    }

    fun onExtractedTextChanged(text: String) {
        _uiState.update { state ->
            state.copy(
                extractedText = text,
                summary = "",
                errorMessage = null,
            )
        }
    }

    fun extractText() {
        val imageUri = _uiState.value.selectedImageUri

        if (imageUri.isNullOrBlank()) {
            showError(
                "Take a photo or choose one from the gallery first.",
            )
            return
        }

        if (_uiState.value.isExtracting) return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isExtracting = true,
                    errorMessage = null,
                )
            }

            runCatching {
                extractTextUseCase(imageUri)
            }.onSuccess { text ->
                _uiState.update { state ->
                    state.copy(
                        extractedText = text,
                        isExtracting = false,
                    )
                }

                _effects.emit(
                    NoteLensEffect.TextExtractionCompleted,
                )
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isExtracting = false,
                        errorMessage = error.userMessage(
                            fallback = "Text extraction failed. Try another photo.",
                        ),
                    )
                }
            }
        }
    }

    fun generateSummary() {
        val noteText = _uiState.value.extractedText

        if (noteText.isBlank()) {
            showError("Add some note text before summarizing.")
            return
        }

        if (_uiState.value.isSummarizing) return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isSummarizing = true,
                    errorMessage = null,
                )
            }

            runCatching {
                generateSummaryUseCase(noteText)
            }.onSuccess { summary ->
                _uiState.update { state ->
                    state.copy(
                        summary = summary,
                        isSummarizing = false,
                    )
                }

                _effects.emit(
                    NoteLensEffect.SummaryGenerationCompleted,
                )
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isSummarizing = false,
                        errorMessage = error.userMessage(
                            fallback = "Summary generation failed. Please try again.",
                        ),
                    )
                }
            }
        }
    }

    fun startOver() {
        _uiState.value = NoteLensUiState(
            isDemoMode = _uiState.value.isDemoMode,
        )
    }

    fun clearError() {
        _uiState.update { state ->
            state.copy(errorMessage = null)
        }
    }

    private fun showError(message: String) {
        _uiState.update { state ->
            state.copy(errorMessage = message)
        }
    }

    private fun Throwable.userMessage(
        fallback: String,
    ): String {
        return message
            ?.takeIf(String::isNotBlank)
            ?: fallback
    }
}