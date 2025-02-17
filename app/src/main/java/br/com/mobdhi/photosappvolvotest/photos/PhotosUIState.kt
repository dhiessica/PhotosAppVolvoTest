package br.com.mobdhi.photosappvolvotest.photos

import br.com.mobdhi.photosappvolvotest.photos.domain.Photo

sealed class PhotosUIState {
    data class Success(
        val name: String,
        val age: String,
        val date: String,
        val photosList: List<Photo>
    ) : PhotosUIState()
    data class Error(
        val message: String?,
        val name: String = "",
        val age: String = "",
        val date: String = "",
        val photosList: List<Photo> = emptyList()
    ) : PhotosUIState()
    data object Loading: PhotosUIState()
}