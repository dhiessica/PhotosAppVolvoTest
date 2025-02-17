package br.com.mobdhi.photosappvolvotest.photos

import br.com.mobdhi.photosappvolvotest.photos.data.PhotoDao
import br.com.mobdhi.photosappvolvotest.photos.data.asModel
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import br.com.mobdhi.photosappvolvotest.photos.domain.asEntity

class PhotosRepositoryImpl(private val photoDao: PhotoDao) : PhotosRepository {
    override suspend fun insertPhoto(photo: Photo): Result<Long> =
        try {
            val insertedPhotoId = photoDao.insertPhoto(photo.asEntity())
            Result.success(insertedPhotoId)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getAllPhotos(): Result<List<Photo>> =
        try {
            val photos = photoDao.getAllPhotos().map { it.asModel() }
            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(e)
        }
}