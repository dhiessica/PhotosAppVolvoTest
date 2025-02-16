package br.com.mobdhi.photosappvolvotest.photos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class PhotosViewModel: ViewModel() {

    private val _uiState = MutableLiveData<PhotosUIState>(PhotosUIState.Loading)
    val uiState: LiveData<PhotosUIState> = _uiState

    private var name = mutableStateOf("")
    private var age = mutableStateOf("")

    fun loadPhotos() = viewModelScope.launch {
        _uiState.postValue(PhotosUIState.Success(
            name = name.value,
            age = age.value,
            date = LocalDate.now().toString(),
            photosList = List(100) { (it+1).toString()}
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
}