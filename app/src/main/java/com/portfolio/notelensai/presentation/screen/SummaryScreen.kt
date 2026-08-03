package com.portfolio.notelensai.presentation.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portfolio.notelensai.presentation.NoteLensUiState
import com.portfolio.notelensai.presentation.NoteLensViewModel
import com.portfolio.notelensai.presentation.components.CommonText
import com.portfolio.notelensai.presentation.components.CommonTopAppBar
import com.portfolio.notelensai.presentation.components.ScreenHeading
import com.portfolio.notelensai.presentation.theme.NoteLensTheme

@Composable
fun SummaryRoute(
    viewModelStoreOwner: ViewModelStoreOwner,
    onStartOver: () -> Unit,
    onBack: () -> Unit,
    viewModel: NoteLensViewModel = hiltViewModel(viewModelStoreOwner),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SummaryScreen(
        state = state,
        onCopy = {
            copySummary(
                context = context,
                summary = state.summary,
            )
        },
        onShare = {
            shareSummary(
                context = context,
                summary = state.summary,
            )
        },
        onStartOver = {
            viewModel.startOver()
            onStartOver()
        },
        onBack = onBack,
    )
}

@Composable
fun SummaryScreen(
    state: NoteLensUiState,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onStartOver: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Your summary",
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
                step = 3,
                title = "Summary ready",
                subtitle = "Copy it, share it, or scan another note.",
            )

            if (state.isDemoMode) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 10.dp,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )

                        CommonText(
                            text = "Demo summary · Add Firebase configuration to use Gemini",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp,
                ),
            ) {
                SelectionContainer {
                    CommonText(
                        text = state.summary,
                        modifier = Modifier.padding(22.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                    enabled = state.summary.isNotBlank(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = null,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )

                    CommonText(text = "Copy")
                }

                FilledTonalButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    enabled = state.summary.isNotBlank(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )

                    CommonText(text = "Share")
                }
            }

            Button(
                onClick = onStartOver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                )

                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                CommonText(text = "Start Over")
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private fun copySummary(
    context: Context,
    summary: String,
) {
    val clipboard = context.getSystemService(
        ClipboardManager::class.java,
    )

    clipboard.setPrimaryClip(
        ClipData.newPlainText(
            "NoteLens summary",
            summary,
        ),
    )

    Toast.makeText(
        context,
        "Summary copied",
        Toast.LENGTH_SHORT,
    ).show()
}

private fun shareSummary(
    context: Context,
    summary: String,
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_SUBJECT,
            "NoteLens AI summary",
        )
        putExtra(
            Intent.EXTRA_TEXT,
            summary,
        )
    }

    context.startActivity(
        Intent.createChooser(
            shareIntent,
            "Share summary",
        ),
    )
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun SummaryPreview() {
    NoteLensTheme(darkTheme = false) {
        SummaryScreen(
            state = NoteLensUiState(
                summary = "• Photosynthesis converts light into chemical energy.\n" +
                        "• Plants use carbon dioxide and release oxygen.",
                isDemoMode = true,
            ),
            onCopy = {},
            onShare = {},
            onStartOver = {},
            onBack = {},
        )
    }
}