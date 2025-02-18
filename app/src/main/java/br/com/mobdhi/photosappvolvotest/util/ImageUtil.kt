package br.com.mobdhi.photosappvolvotest.util

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

object ImageUtil {

    private const val AUTHORITY = "br.com.mobdhi.photosappvolvotest.provider"

    fun getImageUriFromFilename(context: Context, filename: String): Uri? {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val imageFile = File(downloadsDir, filename)

        return if (imageFile.exists()) FileProvider.getUriForFile(
            context,
            AUTHORITY,
            imageFile
        )
        else null
    }

    fun createImageOnDownloadFolder(context: Context): Uri? {
        return try {
            val photoFile = createImageFile()

            if (photoFile != null) {
                FileProvider.getUriForFile(
                    context,
                    AUTHORITY,
                    photoFile
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createImageFile(
        filename: String = "photo_${DateUtil.getTimestampFromDate()}.jpg"
    ): File? {
        try {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val imageFile = File(downloadsDir, filename)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            imageFile.createNewFile()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}