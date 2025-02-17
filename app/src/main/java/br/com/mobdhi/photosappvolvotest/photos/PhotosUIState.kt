package br.com.mobdhi.photosappvolvotest.photos

import br.com.mobdhi.photosappvolvotest.photos.domain.Photo

sealed class PhotosUIState(
    open val name: String = "",
    open val age: String = "",
    open val date: String = "",
    open val photosList: List<Photo> = emptyList()
) {
    data class Success(
        override val name: String,
        override val age: String,
        override val date: String,
        override val photosList: List<Photo>
    ) : PhotosUIState()
    data class Error(
        val message: String?
    ) : PhotosUIState()
    data class Loading(
        override val name: String = "",
        override val age: String = "",
        override val date: String = "",
        override val photosList: List<Photo> = emptyList()
    ): PhotosUIState()
}