package br.com.mobdhi.photosappvolvotest.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.mobdhi.photosappvolvotest.photos.PhotosScreen
import br.com.mobdhi.photosappvolvotest.photos.PhotosViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AppNavHost(navHostController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navHostController,
        startDestination = PhotosRoute
    ) {
        composable<PhotosRoute> {
            val viewModel: PhotosViewModel = getViewModel()

            LaunchedEffect(Unit) { viewModel.loadPhotos() }
            PhotosScreen(
                viewModel = viewModel,
                navigateToTakePictureScreen = {
                    //todo
                }
            )
        }

    }
}