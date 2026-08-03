package com.portfolio.notelensai.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portfolio.notelensai.presentation.NoteLensEffect
import com.portfolio.notelensai.presentation.NoteLensUiState
import com.portfolio.notelensai.presentation.NoteLensViewModel
import com.portfolio.notelensai.presentation.components.CommonText
import com.portfolio.notelensai.presentation.components.CommonTopAppBar
import com.portfolio.notelensai.presentation.components.ErrorMessage
import com.portfolio.notelensai.presentation.components.NoteImage
import com.portfolio.notelensai.presentation.components.ScreenHeading
import com.portfolio.notelensai.presentation.theme.NoteLensTheme
import com.portfolio.notelensai.presentation.util.createNotePhotoUri
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun NoteInputRoute(
    viewModelStoreOwner: ViewModelStoreOwner,
    onTextExtracted: () -> Unit,
    viewModel: NoteLensViewModel = hiltViewModel(viewModelStoreOwner),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects
            .filterIsInstance<NoteLensEffect.TextExtractionCompleted>()
            .collect {
                onTextExtracted()
            }
    }

    NoteInputScreen(
        state = state,
        onImageSelected = viewModel::onImageSelected,
        onExtractText = viewModel::extractText,
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun NoteInputScreen(
    state: NoteLensUiState,
    onImageSelected: (String) -> Unit,
    onExtractText: () -> Unit,
    onDismissError: () -> Unit,
) {
    val context = LocalContext.current
    var pendingCameraUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { saved ->
        if (saved) {
            pendingCameraUri?.let(onImageSelected)
        }

        pendingCameraUri = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val uri = context.createNotePhotoUri()
            pendingCameraUri = uri.toString()
            cameraLauncher.launch(uri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.toString()?.let(onImageSelected)
    }

    val takePhoto = remember(
        context,
        cameraLauncher,
        permissionLauncher,
    ) {
        {
            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                val uri = context.createNotePhotoUri()
                pendingCameraUri = uri.toString()
                cameraLauncher.launch(uri)
            } else {
                permissionLauncher.launch(
                    Manifest.permission.CAMERA,
                )
            }
        }
    }

    NoteInputContent(
        state = state,
        onTakePhoto = takePhoto,
        onChoosePhoto = {
            galleryLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly,
                ),
            )
        },
        onExtractText = onExtractText,
        onDismissError = onDismissError,
    )
}

@Composable
private fun NoteInputContent(
    state: NoteLensUiState,
    onTakePhoto: () -> Unit,
    onChoosePhoto: () -> Unit,
    onExtractText: () -> Unit,
    onDismissError: () -> Unit,
) {
    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "NoteLens AI",
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ScreenHeading(
                step = 1,
                title = "Select your note",
                subtitle = "Use a clear photo with the text filling most of the frame.",
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                if (state.selectedImageUri == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DocumentScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.height(52.dp),
                            )

                            CommonText(
                                text = "Your note preview appears here",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                } else {
                    NoteImage(
                        imageUri = state.selectedImageUri,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onTakePhoto,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isExtracting,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CameraAlt,
                        contentDescription = null,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )

                    CommonText(text = "Camera")
                }

                OutlinedButton(
                    onClick = onChoosePhoto,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isExtracting,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoLibrary,
                        contentDescription = null,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )

                    CommonText(text = "Gallery")
                }
            }

            state.errorMessage?.let { message ->
                ErrorMessage(
                    message = message,
                    onDismiss = onDismissError,
                )
            }

            Button(
                onClick = onExtractText,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = state.selectedImageUri != null &&
                        !state.isExtracting,
            ) {
                if (state.isExtracting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )

                    Spacer(
                        modifier = Modifier.padding(horizontal = 5.dp),
                    )

                    CommonText(text = "Reading note…")
                } else {
                    CommonText(text = "Extract Text")
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
private fun NoteInputPreview() {
    NoteLensTheme(darkTheme = false) {
        NoteInputContent(
            state = NoteLensUiState(),
            onTakePhoto = {},
            onChoosePhoto = {},
            onExtractText = {},
            onDismissError = {},
        )
    }
}