package br.com.mobdhi.photosappvolvotest.photos

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import br.com.mobdhi.photosappvolvotest.util.DateUtil
import br.com.mobdhi.photosappvolvotest.util.ImageUtil
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    navigateToPhotoDetail: (Uri) -> Unit
) {
    val context = LocalContext.current

    val photosList by viewModel.photos.collectAsState()
    val name by viewModel.name.collectAsState()
    val age by viewModel.age.collectAsState()
    val date by viewModel.date.collectAsState()

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
            if (isImageSaved) {
                viewModel.savePhoto()
            } else {
                viewModel.cancelPhoto()
            }
        }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                viewModel.prepareToTakePhoto(context)
                viewModel.imageUri.value?.let {
                    cameraLauncher.launch(it)
                }
            } else {
                val message = context.getString(R.string.permission_denied)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

            }
        }

    PhotosScreenContent(
        name = name,
        age = age,
        date = date,
        photosListUiState = photosList,
        onNameChange = { viewModel.updateName(it) },
        onAgeChange = { viewModel.updateAge(it) },
        onCameraButtonClicked = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onPhotoClicked = navigateToPhotoDetail,
        onDeleteButtonClicked = { viewModel.removePhoto(context,it) }
    )
}

@Composable
fun PhotosScreenContent(
    name: String,
    age: String,
    date: String,
    photosListUiState: PhotosUIState,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onCameraButtonClicked: () -> Unit,
    onPhotoClicked: (Uri) -> Unit,
    onDeleteButtonClicked: (Photo) -> Unit
) {
    when (photosListUiState) {
        is PhotosUIState.Loading -> {
            LoadingCircularProgress()
        }

        is PhotosUIState.Success -> {
            SuccessPhotosContent(
                name = name,
                age = age,
                date = date,
                photosList = photosListUiState.photosList,
                onNameChange = onNameChange,
                onAgeChange = onAgeChange,
                onCameraButtonClicked = onCameraButtonClicked,
                onPhotoClicked = onPhotoClicked,
                onDeleteButtonClicked = onDeleteButtonClicked
            )
        }

        is PhotosUIState.Error -> {
            ErrorMessage(message = photosListUiState.message)
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
    onCameraButtonClicked: () -> Unit,
    onPhotoClicked: (Uri) -> Unit,
    onDeleteButtonClicked: (Photo) -> Unit
) {
    val context = LocalContext.current
    var nameError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Header(
                name = name,
                age = age,
                date = date,
                nameError = nameError,
                ageError = ageError,
                onNameChange = onNameChange,
                onAgeChange = {
                    if (!it.startsWith("0")) onAgeChange(it)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    focusManager.clearFocus()
                    nameError = name.isBlank()
                    ageError = age.isBlank()

                    if (name.isNotBlank() && age.isNotBlank()) {
                        onCameraButtonClicked()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.fill_fields,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
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
            photos = photosList,
            onPhotoClicked = onPhotoClicked,
            onDeleteButtonClicked = onDeleteButtonClicked
        )
    }
}

@Composable
fun PhotosList(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    onPhotoClicked: (Uri) -> Unit,
    onDeleteButtonClicked: (Photo) -> Unit
) {
    if (photos.isNotEmpty())
        LazyColumn(
            state = rememberLazyListState(),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.padding_large))
        ) {
            items(items = photos) { item ->
                Column {
                    if (photos.first() == item) {
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
                    }

                    Column(modifier = Modifier.clickable { onPhotoClicked(item.fileName.toUri()) }) {
                        Row(
                            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium)),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            val imageUri = ImageUtil.getImageUriFromFilename(
                                LocalContext.current, item.fileName
                            )
                            if (imageUri != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "${stringResource(R.string.photo_captured)} ${item.fileName}",
                                    modifier = Modifier.size(100.dp).weight(1f),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(2f)) {
                                Text(
                                    text = item.fileName.substringAfterLast("/"),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))

                                )
                                Text(
                                    text = DateUtil.convertTimestampToLocalDate(item.date),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))

                                )
                                Text(
                                    text = "${item.name} ${stringResource(R.string.age).lowercase()}: ${item.age}",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
                                )
                            }
                            IconButton(
                                onClick = { onDeleteButtonClicked(item) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                        if (photos.last() != item) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    else
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.padding_divisor),
                    start = dimensionResource(R.dimen.padding_large),
                    end = dimensionResource(R.dimen.padding_large)
                )
        ) {
            ErrorMessage(
                icon = null,
                title = stringResource(R.string.empty_title),
                message = stringResource(R.string.empty_message)
            )
        }
}

@Preview(showSystemUi = true)
@Composable
fun PhotosScreenPreview() {
    PhotosScreenContent(
        name = "",
        age = "",
        date = "",
        photosListUiState = PhotosUIState.Success(emptyList()),
        onNameChange = {},
        onAgeChange = {},
        onCameraButtonClicked = {},
        onPhotoClicked = {},
        onDeleteButtonClicked = {}
    )
}