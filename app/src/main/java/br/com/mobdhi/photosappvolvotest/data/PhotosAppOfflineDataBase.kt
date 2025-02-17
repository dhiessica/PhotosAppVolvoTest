package br.com.mobdhi.photosappvolvotest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.mobdhi.photosappvolvotest.photos.data.PhotoDao
import br.com.mobdhi.photosappvolvotest.photos.data.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 1, exportSchema = false)
abstract class PhotosAppOfflineDataBase: RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var Instance: PhotosAppOfflineDataBase? = null
        fun getDatabase(context: Context): PhotosAppOfflineDataBase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PhotosAppOfflineDataBase::class.java,
                    "photos_app_offline_database"
                ).build().also {
                    Instance = it
                }
            }
        }
    }
}