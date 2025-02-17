package br.com.mobdhi.photosappvolvotest.photos

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.sql.Timestamp
import java.text.DateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class PhotosViewModel(
    private val photosRepository: PhotosRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<PhotosUIState>(PhotosUIState.Loading())
    val uiState: LiveData<PhotosUIState> = _uiState

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadAllPhotos() = viewModelScope.launch {
        val currentState = _uiState.value
        _uiState.postValue(PhotosUIState.Loading())

        val result = photosRepository.getAllPhotos()

        if (result.isSuccess) _uiState.postValue(
            PhotosUIState.Success(
                name = currentState?.name ?: "",
                age = currentState?.age ?: "",
                date = getFormattedCurrentDate(),
                photosList = result.getOrNull() ?: emptyList(),
            )
        )
        else _uiState.postValue(
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        )
    }

    fun savePhoto() = viewModelScope.launch {
        val currentState = _uiState.value

        if (currentState is PhotosUIState.Success) {
            _uiState.postValue(PhotosUIState.Loading(currentState.name, currentState.age))

            val newPhoto = Photo(
                name = currentState.name,
                age = currentState.age,
                date = getTimestampFromDate(),
                uri = imageUri.value.toString()
            )
            val result = photosRepository.insertPhoto(newPhoto)

            if (result.isSuccess) loadAllPhotos()
            else _uiState.postValue(
                PhotosUIState.Error(
                    message = result.exceptionOrNull()?.message
                )
            )
        } else _uiState.postValue(
            PhotosUIState.Error(null)
        )
    }

    fun updateField(update: PhotosUIState.Success.() -> PhotosUIState.Success) {
        val currentState = _uiState.value
        if (currentState is PhotosUIState.Success) {
            val updatedState = currentState.update()
            _uiState.postValue(updatedState)
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

    fun removeImageUri() {
        imageUri.value = null
    }

    private fun createImageFile(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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

    private fun getFormattedCurrentDate(): String {
        val currentDate = Date()
        val locale = Locale.getDefault()
        val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale)
        return dateFormat.format(currentDate)
    }

    private fun getTimestampFromDate(date: Date = Date()): Long {
        return date.time
    }

}