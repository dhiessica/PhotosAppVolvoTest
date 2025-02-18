package br.com.mobdhi.photosappvolvotest.photos.domain

import androidx.compose.ui.graphics.ImageBitmap
import br.com.mobdhi.photosappvolvotest.photos.data.PhotoEntity

data class Photo(
    val id: Long = 0,
    val name: String,
    val age: String,
    val date: Long,
    val uri: String,
    val bitmap: ImageBitmap? = null
)

fun Photo.asEntity() = PhotoEntity(
    id = id,
    name = name,
    age = age,
    date = date,
    uri = uri
)
