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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Date

class PhotosViewModel(
    private val photosRepository: PhotosRepository
) : ViewModel() {

    var uiState = MutableStateFlow<PhotosUIState>(PhotosUIState.Loading)

    var name = MutableStateFlow("")

    var age = MutableStateFlow("")

    var date = MutableStateFlow(DateUtil.getFormattedCurrentDate())

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadAllPhotos(context: Context) = viewModelScope.launch {
        uiState.update { PhotosUIState.Loading }

        val result = withContext(Dispatchers.IO) { photosRepository.getAllPhotos() }

        if (result.isSuccess) uiState.update {
            PhotosUIState.Success(
                photosList = result.getOrNull()
                    ?.map {
                        Photo(
                            id = it.id,
                            name = it.name,
                            age = it.age,
                            date = it.date,
                            uri = it.uri,
                            bitmap = withContext(Dispatchers.IO) {
                                ImageUtil.loadImageBitmapFromUri(it.uri.toUri(), context)
                            }
                        )
                    }
                    ?: emptyList(),
            )
        }

        else uiState.update {
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        }
    }

    fun savePhoto(context: Context) = viewModelScope.launch {
        uiState.update { PhotosUIState.Loading }

        val newPhoto = Photo(
            name = name.value,
            age = age.value,
            date = DateUtil.getTimestampFromDate(),
            uri = imageUri.value.toString()
        )

        val result = withContext(Dispatchers.IO) { photosRepository.insertPhoto(newPhoto) }

        if (result.isSuccess) loadAllPhotos(context)
        else uiState.update {
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        }
    }

    fun updateName(newName: String) {
        name.value = newName
    }

    fun updateAge(newAge: String) {
        age.value = newAge
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