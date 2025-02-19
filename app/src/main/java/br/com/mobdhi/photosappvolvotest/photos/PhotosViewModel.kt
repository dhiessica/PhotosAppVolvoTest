package br.com.mobdhi.photosappvolvotest.photos

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.usecase.GetAllPhotosUseCase
import br.com.mobdhi.photosappvolvotest.photos.usecase.RemovePhotoUseCase
import br.com.mobdhi.photosappvolvotest.photos.usecase.SavePhotoUseCase
import br.com.mobdhi.photosappvolvotest.util.DateUtil
import br.com.mobdhi.photosappvolvotest.util.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class PhotosViewModel(
    private val getPhotosUseCase: GetAllPhotosUseCase,
    private val savePhotoUseCase: SavePhotoUseCase,
    private val removePhotoUseCase: RemovePhotoUseCase
) : ViewModel() {

    val photos = MutableStateFlow<PhotosUIState>(PhotosUIState.Loading)

    val name = MutableStateFlow("")

    val age = MutableStateFlow("")

    val date = MutableStateFlow(DateUtil.getFormattedDate(Date()))

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadAllPhotos() = viewModelScope.launch {
        photos.update { PhotosUIState.Loading }

        val result = withContext(Dispatchers.IO) { getPhotosUseCase() }

        if (result.isSuccess) photos.update {
            PhotosUIState.Success(
                photosList = result.getOrNull()
                    ?.map {
                        Photo(
                            id = it.id,
                            name = it.name,
                            age = it.age,
                            date = it.date,
                            fileName = it.fileName,
                        )
                    }
                    ?: emptyList(),
            )
        }
        else photos.update {
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        }
    }

    fun savePhoto() = viewModelScope.launch {
        photos.update { PhotosUIState.Loading }

        val newPhoto = Photo(
            name = name.value,
            age = age.value,
            date = DateUtil.getTimestampFromDate(),
            fileName = imageUri.value.toString().substringAfterLast("/")
        )

        val result = withContext(Dispatchers.IO) { savePhotoUseCase(newPhoto) }

        if (result.isSuccess) loadAllPhotos()
        else photos.update {
            PhotosUIState.Error(
                message = result.exceptionOrNull()?.message
            )
        }
    }

    fun removePhoto(context: Context, photo: Photo) = viewModelScope.launch {
        photos.update { PhotosUIState.Loading }

        val result = withContext(Dispatchers.IO) { removePhotoUseCase(context, photo) }

        if (result.isSuccess) loadAllPhotos()
        else photos.update {
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

    fun prepareToTakePhoto(context: Context) {
        imageUri.value = ImageUtil.createImageOnDownloadFolder(context)
    }

    fun cancelPhoto() {
        imageUri.value = if (imageUri.value?.let { ImageUtil.removeImageOnDownloadFolder(it) } == true) null else Uri.EMPTY
    }

}