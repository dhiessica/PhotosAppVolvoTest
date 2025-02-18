package br.com.mobdhi.photosappvolvotest.photos

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import br.com.mobdhi.photosappvolvotest.util.DateUtil
import br.com.mobdhi.photosappvolvotest.util.ImageUtil
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.Date

class PhotosViewModel(
    private val photosRepository: PhotosRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<PhotosUIState>(PhotosUIState.Loading)
    val uiState: LiveData<PhotosUIState> = _uiState

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _age = MutableLiveData<String>()
    val age: LiveData<String> = _age

    private val _date = MutableLiveData(DateUtil.getFormattedCurrentDate())
    val date: LiveData<String> = _date

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadAllPhotos(context: Context) = viewModelScope.launch {
        _uiState.postValue(PhotosUIState.Loading)

        val result = photosRepository.getAllPhotos()

        if (result.isSuccess) _uiState.postValue(
            PhotosUIState.Success(
                photosList = result.getOrNull()
                    ?.map {Photo(
                        id = it.id,
                        name = it.name,
                        age = it.age,
                        date = it.date,
                        uri = it.uri,
                        bitmap = ImageUtil.loadImageBitmapFromUri(it.uri.toUri(), context)
                    )
                    }
                    ?: emptyList(),
            )
        )
        else _uiState.postValue(
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        )
    }

    fun savePhoto(context: Context) = viewModelScope.launch {
        _uiState.postValue(PhotosUIState.Loading)

        val newPhoto = Photo(
            name = _name.value ?: "",
            age = _age.value ?: "",
            date = DateUtil.getTimestampFromDate(),
            uri = imageUri.value.toString()
        )

        val result = photosRepository.insertPhoto(newPhoto)

        if (result.isSuccess) loadAllPhotos(context)
        else _uiState.postValue(
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        )
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateAge(newAge: String) {
        _age.value = newAge
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

}