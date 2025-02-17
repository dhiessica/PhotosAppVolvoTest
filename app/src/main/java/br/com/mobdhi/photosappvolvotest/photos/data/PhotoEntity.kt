package br.com.mobdhi.photosappvolvotest.photos.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import java.sql.Date
import java.sql.Timestamp

@Entity(tableName = "photo")
class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: String,
    val date: Long,
    val uri: String
)

fun PhotoEntity.asModel() = Photo(
    id = id,
    name = name,
    age = age,
    date = date,
    uri = uri
)
