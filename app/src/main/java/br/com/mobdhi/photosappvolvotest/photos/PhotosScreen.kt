package br.com.mobdhi.photosappvolvotest.photos

import android.Manifest
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import br.com.mobdhi.photosappvolvotest.R
import br.com.mobdhi.photosappvolvotest.components.ErrorMessage
import br.com.mobdhi.photosappvolvotest.components.Header
import br.com.mobdhi.photosappvolvotest.components.LoadingCircularProgress
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo

@Composable
fun PhotosScreen(viewModel: PhotosViewModel, ) {
    val uiState by viewModel.uiState.observeAsState(PhotosUIState.Loading)
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            viewModel.savePhoto()
        } else {
            viewModel.removeImageUri()
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            viewModel.generateImageUri(context)
            viewModel.imageUri.value?.let {
                cameraLauncher.launch(it)
            }
        } else {
            val message = context.getString(R.string.permission_denied)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        }
    }

    PhotosScreenContent(
        uiState = uiState,
        onNameChange = { viewModel.updateField { copy(name = it) } },
        onAgeChange = { viewModel.updateField { copy(age = it) } },
        onCameraButtonClicked = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    )
}

@Composable
fun PhotosScreenContent(
    uiState: PhotosUIState,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onCameraButtonClicked: () -> Unit,
) {
    when (uiState) {
        is PhotosUIState.Loading -> {
            LoadingCircularProgress()
        }

        is PhotosUIState.Success -> {
            SuccessPhotosContent(
                name = uiState.name,
                age = uiState.age,
                date = uiState.date,
                photosList = uiState.photosList,
                onNameChange = onNameChange,
                onAgeChange = onAgeChange,
                onCameraButtonClicked = onCameraButtonClicked
            )
        }

        is PhotosUIState.Error -> {
            ErrorMessage(message = uiState.message)
        }
    }
}

@Composable
fun SuccessPhotosContent(
    name: String,
    age: String,
    date: String,
    photosList: List<Photo>,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onCameraButtonClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            Header(
                name = name,
                age = age,
                date = date,
                onNameChange = onNameChange,
                onAgeChange = onAgeChange
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCameraButtonClicked,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = stringResource(R.string.take_photo)
                )
            }
        },
    ) { innerPadding ->

        PhotosList(
            modifier = Modifier.padding(innerPadding),
            photos = photosList
        )
    }
}

@Composable
fun PhotosList(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    onPhotoClicked: (Uri?) -> Unit = {}
) {
    if (photos.isNotEmpty())
        LazyColumn(
            state = rememberLazyListState(),
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_large),
                    end = dimensionResource(R.dimen.padding_large)
                )
        ) {
            items(items = photos) { item ->
                Column(modifier = Modifier.clickable { onPhotoClicked(item.uri.toUri()) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val source = ImageDecoder.createSource(LocalContext.current.contentResolver, item.uri.toUri())
                        val bitmap = ImageDecoder.decodeBitmap(source)

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.photo_captured),
                            modifier = Modifier.height(100.dp),
                            contentScale = ContentScale.FillHeight
                        )

                        Text(
                            text = item.toString(),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                    if(photos.last() != item) {
                        HorizontalDivider()
                    }
                }
            }
        }
    else
        ErrorMessage(
            title = stringResource(R.string.empty_title),
            message = stringResource(R.string.empty_message)
        )
}

@Preview(showSystemUi = true)
@Composable
fun PhotosScreenPreview() {
    PhotosScreenContent(
        uiState = PhotosUIState.Loading,
        onNameChange = {},
        onAgeChange = {},
        onCameraButtonClicked = {},
    )
}