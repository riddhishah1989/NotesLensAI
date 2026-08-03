package com.portfolio.notelensai.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portfolio.notelensai.presentation.NoteLensEffect
import com.portfolio.notelensai.presentation.NoteLensUiState
import com.portfolio.notelensai.presentation.NoteLensViewModel
import com.portfolio.notelensai.presentation.components.CommonOutlinedTextField
import com.portfolio.notelensai.presentation.components.CommonText
import com.portfolio.notelensai.presentation.components.CommonTopAppBar
import com.portfolio.notelensai.presentation.components.ErrorMessage
import com.portfolio.notelensai.presentation.components.ScreenHeading
import com.portfolio.notelensai.presentation.theme.NoteLensTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun ExtractedTextRoute(
    viewModelStoreOwner: ViewModelStoreOwner,
    onSummaryGenerated: () -> Unit,
    onBack: () -> Unit,
    viewModel: NoteLensViewModel = hiltViewModel(viewModelStoreOwner),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects
            .filterIsInstance<NoteLensEffect.SummaryGenerationCompleted>()
            .collect {
                onSummaryGenerated()
            }
    }

    ExtractedTextScreen(
        state = state,
        onTextChanged = viewModel::onExtractedTextChanged,
        onGenerateSummary = viewModel::generateSummary,
        onDismissError = viewModel::clearError,
        onBack = onBack,
    )
}

@Composable
fun ExtractedTextScreen(
    state: NoteLensUiState,
    onTextChanged: (String) -> Unit,
    onGenerateSummary: () -> Unit,
    onDismissError: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Review text",
                onBack = onBack,
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ScreenHeading(
                step = 2,
                title = "Check the text",
                subtitle = "Fix any OCR mistakes before sending the note to Gemini.",
            )

            CommonOutlinedTextField(
                value = state.extractedText,
                onValueChange = onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                label = "Extracted note",
                supportingText = "${state.extractedText.length} characters",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                enabled = !state.isSummarizing,
            )

            state.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = onDismissError,
                )
            }

            Button(
                onClick = onGenerateSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = state.extractedText.isNotBlank() &&
                        !state.isSummarizing,
            ) {
                if (state.isSummarizing) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 5.dp),
                    )

                    CommonText(text = "Creating summary…")
                } else {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )

                    CommonText(text = "Generate Summary")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun ExtractedTextPreview() {
    NoteLensTheme(darkTheme = false) {
        ExtractedTextScreen(
            state = NoteLensUiState(
                extractedText = "Photosynthesis converts light energy into chemical energy.",
            ),
            onTextChanged = {},
            onGenerateSummary = {},
            onDismissError = {},
            onBack = {},
        )
    }
}