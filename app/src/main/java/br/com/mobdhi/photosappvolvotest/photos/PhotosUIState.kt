package br.com.mobdhi.photosappvolvotest.photos

import android.net.Uri

sealed class PhotosUIState {
    data class Success(
        val name: String,
        val age: String,
        val date: String,
        val photosList: List<Uri?>
    ) : PhotosUIState()
    data class Error(
        val message: String,
        val name: String = "",
        val age: String = "",
        val date: String = "",
        val photosList: List<Uri?> = emptyList()
    ) : PhotosUIState()
    data object Loading: PhotosUIState()
}