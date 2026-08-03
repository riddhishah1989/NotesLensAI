package com.portfolio.notelensai.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

fun Context.createNotePhotoUri(): Uri {
    val photoDirectory = File(cacheDir, "note_photos").apply { mkdirs() }
    val photoFile = File.createTempFile("note_", ".jpg", photoDirectory)
    return FileProvider.getUriForFile(
        this,
        "$packageName.fileprovider",
        photoFile,
    )
}

fun Context.decodeNoteImage(uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val width = info.size.width
            val height = info.size.height
            val largestSide = maxOf(width, height)
            if (largestSide > MAX_PREVIEW_SIZE) {
                val scale = MAX_PREVIEW_SIZE.toFloat() / largestSide
                decoder.setTargetSize(
                    (width * scale).toInt(),
                    (height * scale).toInt(),
                )
            }
        }
    } else {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri).use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds)
        }
        contentResolver.openInputStream(uri).use { stream ->
            requireNotNull(BitmapFactory.decodeStream(stream, null, options)) {
                "The selected image could not be opened."
            }
        }
    }
}

private fun calculateSampleSize(options: BitmapFactory.Options): Int {
    var sampleSize = 1
    var width = options.outWidth
    var height = options.outHeight
    while (width / 2 >= MAX_PREVIEW_SIZE || height / 2 >= MAX_PREVIEW_SIZE) {
        width /= 2
        height /= 2
        sampleSize *= 2
    }
    return sampleSize
}

private const val MAX_PREVIEW_SIZE = 1_600

