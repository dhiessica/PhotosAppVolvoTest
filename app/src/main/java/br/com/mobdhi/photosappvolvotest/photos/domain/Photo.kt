package br.com.mobdhi.photosappvolvotest.photos.domain

import br.com.mobdhi.photosappvolvotest.photos.data.PhotoEntity
import java.sql.Date
import java.sql.Timestamp

data class Photo(
    val id: Long = 0,
    val name: String,
    val age: String,
    val date: Long,
    val uri: String
)

fun Photo.asEntity() = PhotoEntity(
    id = id,
    name = name,
    age = age,
    date = date,
    uri = uri
)
