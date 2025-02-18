package br.com.mobdhi.photosappvolvotest.photos

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import br.com.mobdhi.photosappvolvotest.photos.usecase.GetAllPhotosUseCase
import br.com.mobdhi.photosappvolvotest.util.DateUtil
import br.com.mobdhi.photosappvolvotest.util.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotosViewModel(
    private val photosRepository: PhotosRepository,
    private val getPhotosUseCase: GetAllPhotosUseCase,
) : ViewModel() {

    var photos = MutableStateFlow<PhotosUIState>(PhotosUIState.Loading)

    var name = MutableStateFlow("")

    var age = MutableStateFlow("")

    var date = MutableStateFlow(DateUtil.getFormattedCurrentDate())

    var imageUri: MutableState<Uri?> = mutableStateOf(Uri.EMPTY)
        private set

    fun loadAllPhotos(context: Context) = viewModelScope.launch {
        photos.update { PhotosUIState.Loading }

        val result = withContext(Dispatchers.IO) { getPhotosUseCase.getAllPhotos(context) }

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

    fun savePhoto(context: Context) = viewModelScope.launch {
        photos.update { PhotosUIState.Loading }

        val newPhoto = Photo(
            name = name.value,
            age = age.value,
            date = DateUtil.getTimestampFromDate(),
            fileName = imageUri.value.toString().substringAfterLast("/")
        )

        val result = withContext(Dispatchers.IO) { photosRepository.insertPhoto(newPhoto) }

        if (result.isSuccess) loadAllPhotos(context)
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

    fun generateImage(context: Context) {
        imageUri.value = ImageUtil.createImageOnDownloadFolder(context)
    }

    fun removeImage() {
        imageUri.value = if (imageUri.value?.let { ImageUtil.removeImageOnDownloadFolder(it) } == true) null else Uri.EMPTY
    }

}