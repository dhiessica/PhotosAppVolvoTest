package br.com.mobdhi.photosappvolvotest.app.navigation

import kotlinx.serialization.Serializable

@Serializable
object PhotosRoute

@Serializable
data class PhotoDetailRoute(val imageName: String)