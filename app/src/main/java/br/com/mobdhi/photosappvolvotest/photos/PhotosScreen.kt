package br.com.mobdhi.photosappvolvotest.photos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.mobdhi.photosappvolvotest.R
import br.com.mobdhi.photosappvolvotest.components.ErrorMessage
import br.com.mobdhi.photosappvolvotest.components.Header
import br.com.mobdhi.photosappvolvotest.components.LoadingCircularProgress
import org.koin.androidx.compose.getViewModel

@Composable
fun PhotosScreen(
    viewModel: PhotosViewModel,
    navigateToTakePictureScreen: () -> Unit
) {
    val uiState by viewModel.uiState.observeAsState(PhotosUIState.Loading)

    PhotosScreenContent(
        uiState = uiState,
        onNameChange = { viewModel.updateField { copy(name = it) } },
        onAgeChange = { viewModel.updateField { copy(age = it) } },
        onCameraButtonClicked = navigateToTakePictureScreen
    )
}

@Composable
fun PhotosScreenContent(
    uiState: PhotosUIState,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onCameraButtonClicked: () -> Unit
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
    photosList: List<String>,
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
                    contentDescription = stringResource(R.string.take_picture)
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
    photos: List<String>,
    onPhotoClicked: (String) -> Unit = {}
) {
    if (photos.isNotEmpty())
        LazyColumn(
            state = rememberLazyListState(),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.padding_large))
        ) {
            items(items = photos) { item ->
                Column(
                    modifier = Modifier.clickable { onPhotoClicked(item) }
                ) {
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
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
        onCameraButtonClicked = {}
    )
}