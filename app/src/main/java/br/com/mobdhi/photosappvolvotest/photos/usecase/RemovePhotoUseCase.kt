package br.com.mobdhi.photosappvolvotest.photos.usecase

import android.content.Context
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import br.com.mobdhi.photosappvolvotest.util.ImageUtil

class RemovePhotoUseCase(private val photoRepository: PhotosRepository) {
    suspend operator fun invoke(context: Context, photo: Photo): Result<Int> =
        try {
            val result = photoRepository.deletePhoto(photo)

            if (result.isSuccess) {
                ImageUtil.getImageUriFromFilename(context, photo.fileName)
                    ?.let { ImageUtil.removeImageOnDownloadFolder(it) }
            }
            result

        } catch (e: Exception) {
            Result.failure(e)
        }



}