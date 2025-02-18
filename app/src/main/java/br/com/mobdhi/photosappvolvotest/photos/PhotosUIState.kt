package br.com.mobdhi.photosappvolvotest.photos

import br.com.mobdhi.photosappvolvotest.photos.domain.Photo

sealed class PhotosUIState(
    open val photosList: List<Photo> = emptyList()
) {
    data class Success(
        override val photosList: List<Photo>
    ) : PhotosUIState()
    data class Error(
        val message: String?
    ) : PhotosUIState()
    data object Loading: PhotosUIState()
}