package br.com.mobdhi.photosappvolvotest.photos.usecase

import android.content.Context
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import br.com.mobdhi.photosappvolvotest.util.ImageUtil

class GetAllPhotosUseCase(private val photoRepository: PhotosRepository) {
    suspend fun getAllPhotos(context: Context): Result<List<Photo>> =
        try {
            val result = photoRepository.getAllPhotos().map { it }

            if (result.isSuccess) {
                val validPhotos: MutableList<Photo> = mutableListOf()

                result.getOrNull()?.forEach { photo ->
                    val imageFileExist = ImageUtil.getImageFileFromFilename(context, photo.fileName)
                        ?.exists() == true
                    if (!imageFileExist) photoRepository.deletePhoto(photo)
                    else validPhotos.add(photo)

                }

                Result.success(validPhotos)

            } else result

        } catch (e: Exception) {
            Result.failure(e)
        }
}