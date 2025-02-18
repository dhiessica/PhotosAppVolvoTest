package br.com.mobdhi.photosappvolvotest.photos.usecase

import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository

class SavePhotoUseCase(private val photoRepository: PhotosRepository) {
    suspend operator fun invoke(photo: Photo): Result<Long> = photoRepository.insertPhoto(photo)

}