package br.com.mobdhi.photosappvolvotest.photos

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.time.LocalDate

class PhotosViewModel: ViewModel() {

    private val _uiState = MutableLiveData<PhotosUIState>(PhotosUIState.Loading)
    val uiState: LiveData<PhotosUIState> = _uiState

    private var name = mutableStateOf("")
    private var age = mutableStateOf("")

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadPhotos() = viewModelScope.launch {
        _uiState.postValue(PhotosUIState.Success(
            name = name.value,
            age = age.value,
            date = LocalDate.now().toString(),
            photosList = emptyList()
        ))
    }

    fun updateField(update: PhotosUIState.Success.() -> PhotosUIState.Success) {
        val currentState = _uiState.value
        if (currentState is PhotosUIState.Success) {
            val updatedState = currentState.update()
            _uiState.postValue(updatedState)
            name.value = updatedState.name
            age.value = updatedState.age
        }
    }
    fun generateImageUri(context: Context) {
        val photoFile = createImageFile()
        imageUri.value = FileProvider.getUriForFile(
            context,
            "br.com.mobdhi.photosappvolvotest.provider",
            photoFile
        )
    }
    fun savePhoto(newPhotoUri: Uri? = imageUri.value) {
        _uiState.value?.let { currentState ->
            if (currentState is PhotosUIState.Success) {
                val updatedPhotosList = currentState.photosList.toMutableList().apply {
                    add(newPhotoUri)
                }
                _uiState.postValue(
                    currentState.copy(photosList = updatedPhotosList)
                )
            }
        }
    }

    fun removeImageUri() {
        imageUri.value = null
    }

    private fun createImageFile(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val imageFile = File(downloadsDir, "photo_${System.currentTimeMillis()}.jpg")
        try {
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            imageFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFile
    }



}