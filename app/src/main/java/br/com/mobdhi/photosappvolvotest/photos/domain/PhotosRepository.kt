package br.com.mobdhi.photosappvolvotest.photos.domain

interface PhotosRepository {
    suspend fun insertPhoto(photo: Photo): Result<Long>
    suspend fun getAllPhotos(): Result<List<Photo>>
}