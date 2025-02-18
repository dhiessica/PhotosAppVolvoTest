package br.com.mobdhi.photosappvolvotest.util

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object ImageUtil {
    fun loadImageBitmapFromUri(uri: Uri, context: Context): ImageBitmap? {
        return try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source).asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}