package br.com.mobdhi.photosappvolvotest.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.util.Date

object ImageUtil {

    private const val AUTHORITY = "br.com.mobdhi.photosappvolvotest.provider"

    fun getImageFileFromFilename(filename: String): File? {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val imageFile = File(downloadsDir, filename)

        return if (imageFile.exists()) imageFile
        else null
    }

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

    fun removeImageOnDownloadFolder(uri: Uri): Boolean {
        return try {
            uri.path?.let {
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val imageFile = File(downloadsDir, uri.toString().substringAfterLast("/"))
                if (imageFile.exists()) {
                    imageFile.delete()
                }
                return !imageFile.exists()
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun createImageFile(
        filename: String = "photo_${Date().time}.jpg"
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