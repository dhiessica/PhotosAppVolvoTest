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
): ViewModel() {

    private val _uiState = MutableLiveData<PhotosUIState>(PhotosUIState.Loading)
    val uiState: LiveData<PhotosUIState> = _uiState

    private var name = mutableStateOf("")
    private var age = mutableStateOf("")
    private var date = mutableStateOf(getFormattedCurrentDate())

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadAllPhotos() = viewModelScope.launch {
        _uiState.postValue(PhotosUIState.Loading)

        val result = photosRepository.getAllPhotos()

        if (result.isSuccess) _uiState.postValue(
            PhotosUIState.Success(
                name = name.value,
                age = age.value,
                date = date.value,
                photosList = result.getOrNull() ?: emptyList()
            )
        )
        else _uiState.postValue(
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        )
    }

    fun savePhoto(newPhoto: Photo = Photo(name = name.value, age = age.value, date = getTimestampFromDate(), uri = imageUri.value.toString())) = viewModelScope.launch {
        _uiState.postValue(PhotosUIState.Loading)

        val result = photosRepository.insertPhoto(newPhoto)

        if (result.isSuccess) loadAllPhotos()
        else _uiState.postValue(
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        )
    }
    fun updateField(update: PhotosUIState.Success.() -> PhotosUIState.Success) {
        val currentState = _uiState.value
        if (currentState is PhotosUIState.Success) {
            val updatedState = currentState.update()
            _uiState.postValue(updatedState)
            name.value = updatedState.name
            age.value = updatedState.age
            date.value = getFormattedCurrentDate()
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