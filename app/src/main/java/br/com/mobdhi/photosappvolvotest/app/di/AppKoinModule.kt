package br.com.mobdhi.photosappvolvotest.app.di

import br.com.mobdhi.photosappvolvotest.photos.PhotosViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appKoinModule() = module {
    viewModel { PhotosViewModel() }
}