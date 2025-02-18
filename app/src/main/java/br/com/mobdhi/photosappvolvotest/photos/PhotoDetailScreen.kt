package br.com.mobdhi.photosappvolvotest.photos

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.mobdhi.photosappvolvotest.R
import br.com.mobdhi.photosappvolvotest.components.ErrorMessage
import br.com.mobdhi.photosappvolvotest.components.TopAppBar
import br.com.mobdhi.photosappvolvotest.util.ImageUtil
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    imageName: String,
    navigateUp: () -> Unit
) {
    val imageUri =
        ImageUtil.getImageUriFromFilename(LocalContext.current, imageName)

    Scaffold(
        topBar = {
            TopAppBar(
                title = imageName,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->

        if (imageUri != null) {
            ImageDetailSuccess(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                imageUri = imageUri,
            )
        } else
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = dimensionResource(R.dimen.padding_divisor),
                        start = dimensionResource(R.dimen.padding_large),
                        end = dimensionResource(R.dimen.padding_large)
                    )
            ) {
                PhotoDetailError()
            }
    }

}

@Composable
fun ImageDetailSuccess(
    imageUri: Uri,
    modifier: Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .crossfade(true)
            .build(),
        contentDescription = "${stringResource(R.string.photo_captured)} ${
            imageUri.toString().substringAfterLast("/")
        }",
        modifier = modifier
            .fillMaxSize(),
        contentScale = ContentScale.Inside
    )
}

@Composable
fun PhotoDetailError() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = dimensionResource(R.dimen.padding_divisor),
                start = dimensionResource(R.dimen.padding_large),
                end = dimensionResource(R.dimen.padding_large)
            )
    ) {
        ErrorMessage(message = stringResource(R.string.error_view_photo_message))
    }
}