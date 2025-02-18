package br.com.mobdhi.photosappvolvotest.photos.domain

import br.com.mobdhi.photosappvolvotest.photos.data.PhotoEntity

data class Photo(
    val id: Long = 0,
    val name: String,
    val age: String,
    val date: Long,
    val fileName: String,
)

fun Photo.asEntity() = PhotoEntity(
    id = id,
    name = name,
    age = age,
    date = date,
    uri = fileName
)
