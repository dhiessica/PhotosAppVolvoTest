package br.com.mobdhi.photosappvolvotest.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.mobdhi.photosappvolvotest.photos.PhotoDetailScreen
import br.com.mobdhi.photosappvolvotest.photos.PhotosScreen
import br.com.mobdhi.photosappvolvotest.photos.PhotosUIState
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
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                if (viewModel.photos.value is PhotosUIState.Loading) {
                    viewModel.loadAllPhotos(context)
                }
            }

            PhotosScreen(
                viewModel = viewModel,
                navigateToPhotoDetail = {
                    navHostController.navigate(PhotoDetailRoute(it.toString()))
                }
            )
        }

        composable<PhotoDetailRoute> { backStackEntry ->
            val routeArguments = backStackEntry.toRoute<PhotoDetailRoute>()

            PhotoDetailScreen(
                imageName = routeArguments.imageName,
                navigateUp = { navHostController.navigateUp() }
            )

        }
    }
}