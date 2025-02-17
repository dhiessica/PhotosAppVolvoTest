package br.com.mobdhi.photosappvolvotest.photos

import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import br.com.mobdhi.photosappvolvotest.R
import br.com.mobdhi.photosappvolvotest.components.TopAppBar

@Composable
fun PhotoDetailScreen(
    imageName: String,
    navigateUp: () -> Unit
) {
    ImageDetailSuccess(
        imageUri = imageName.toUri(),
        navigateUp = navigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailSuccess(
    imageUri: Uri,
    navigateUp: () -> Unit
) {
    val source = ImageDecoder.createSource(
        LocalContext.current.contentResolver,
        imageUri
    )
    val bitmap = ImageDecoder.decodeBitmap(source)

    Scaffold(
        topBar = {
            TopAppBar(
                title = imageUri.toString().substringAfterLast("/"),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
    ) { innerPadding ->

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "${stringResource(R.string.photo_captured)} ${imageUri.toString().substringAfterLast("/")}",
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentScale = ContentScale.FillHeight
        )

    }
}