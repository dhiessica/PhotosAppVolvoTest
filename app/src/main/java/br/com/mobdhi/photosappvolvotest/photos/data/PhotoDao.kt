package br.com.mobdhi.photosappvolvotest.photos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Query("SELECT * FROM photo ORDER BY id DESC")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity): Int

}