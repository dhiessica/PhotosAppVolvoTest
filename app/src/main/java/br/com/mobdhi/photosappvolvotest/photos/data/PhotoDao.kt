package br.com.mobdhi.photosappvolvotest.photos.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Query("SELECT * FROM photo")
    suspend fun getAllPhotos(): List<PhotoEntity>

}