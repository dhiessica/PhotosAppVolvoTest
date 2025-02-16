package br.com.mobdhi.photosappvolvotest.photos
sealed class PhotosUIState {
    data class Success(
        val name: String,
        val age: String,
        val date: String,
        val photosList: List<String>
    ) : PhotosUIState()
    data class Error(
        val message: String,
        val name: String = "",
        val age: String = "",
        val date: String = "",
        val photosList: List<String> = emptyList()
    ) : PhotosUIState()
    data object Loading: PhotosUIState()
}