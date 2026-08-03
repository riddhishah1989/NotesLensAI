package com.portfolio.notelensai.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.portfolio.notelensai.presentation.util.decodeNoteImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NoteImage(
    imageUri: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val result by produceState<Result<android.graphics.Bitmap>?>(null, imageUri) {
        value = withContext(Dispatchers.IO) {
            runCatching { context.decodeNoteImage(Uri.parse(imageUri)) }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when {
            result == null -> CircularProgressIndicator()
            result?.isSuccess == true -> Image(
                bitmap = checkNotNull(result?.getOrNull()).asImageBitmap(),
                contentDescription = "Selected note",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            else -> Icon(
                imageVector = Icons.Rounded.BrokenImage,
                contentDescription = "Unable to display selected note",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

