package br.com.mobdhi.photosappvolvotest.app.di

import br.com.mobdhi.photosappvolvotest.data.PhotosAppOfflineDataBase
import br.com.mobdhi.photosappvolvotest.photos.repository.PhotosRepositoryImpl
import br.com.mobdhi.photosappvolvotest.photos.PhotosViewModel
import br.com.mobdhi.photosappvolvotest.photos.domain.PhotosRepository
import br.com.mobdhi.photosappvolvotest.photos.usecase.GetAllPhotosUseCase
import br.com.mobdhi.photosappvolvotest.photos.usecase.SavePhotoUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appKoinModule() = module {

    single<PhotosRepository> {
        PhotosRepositoryImpl(
            PhotosAppOfflineDataBase
                .getDatabase(androidContext()).photoDao()
        )
    }

    single { GetAllPhotosUseCase(get()) }
    single { SavePhotoUseCase(get()) }

    viewModel { PhotosViewModel(get(), get()) }
}