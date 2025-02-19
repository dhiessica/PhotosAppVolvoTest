package br.com.mobdhi.photosappvolvotest.photos

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.FileProvider
import br.com.mobdhi.photosappvolvotest.photos.domain.Photo
import br.com.mobdhi.photosappvolvotest.photos.usecase.GetAllPhotosUseCase
import br.com.mobdhi.photosappvolvotest.photos.usecase.RemovePhotoUseCase
import br.com.mobdhi.photosappvolvotest.photos.usecase.SavePhotoUseCase
import br.com.mobdhi.photosappvolvotest.util.DateUtil
import br.com.mobdhi.photosappvolvotest.util.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PhotosViewModelTest {

    @Mock
    private lateinit var getPhotosUseCase: GetAllPhotosUseCase
    @Mock
    private lateinit var savePhotoUseCase: SavePhotoUseCase
    @Mock
    private lateinit var removePhotoUseCase: RemovePhotoUseCase
    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var imageUtil: ImageUtil
    @Mock
    private lateinit var packageManager: PackageManager

    private val testDispatcher = UnconfinedTestDispatcher()


    @InjectMocks
    private lateinit var viewModel: PhotosViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDrop() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadAllPhotos success`(): Unit = runBlocking {
        val photosList = listOf(
            Photo(
                name = "Dhiessica",
                age = "24",
                date = DateUtil.getTimestampFromDate(),
                fileName = "new_photo.jpg"
            )
        )
        `when`(getPhotosUseCase()).thenReturn(Result.success(photosList))

        viewModel.loadAllPhotos()

        val state = viewModel.photos.first { it is PhotosUIState.Success }
        assert(state is PhotosUIState.Success)
        assert((state as PhotosUIState.Success).photosList.size == 1)
    }

    @Test
    fun `test loadAllPhotos failure`(): Unit = runBlocking {
        `when`(getPhotosUseCase()).thenReturn(Result.failure(Exception("Error fetching photos")))

        viewModel.loadAllPhotos()

        val state = viewModel.photos.first()
        assert(state is PhotosUIState.Error)
        assert((state as PhotosUIState.Error).message == "Error fetching photos")
    }

    @Test
    fun `test savePhoto success`(): Unit = runBlocking {
        val newPhoto = Photo(
            name = "Dhiessica",
            age = "24",
            date = DateUtil.getTimestampFromDate(),
            fileName = "new_photo.jpg"
        )
        `when`(savePhotoUseCase(newPhoto)).thenReturn(Result.success(1L))

        viewModel.savePhoto(newPhoto)

        val state = viewModel.photos.first { it is PhotosUIState.Success }
        assert(state is PhotosUIState.Success)
        verify(savePhotoUseCase).invoke(newPhoto)
    }

    @Test
    fun `test savePhoto failure`():Unit = runBlocking {
        val newPhoto = Photo(
            name = "New Photo",
            age = "30",
            date = DateUtil.getTimestampFromDate(),
            fileName = "new_photo.jpg"
        )

        `when`(savePhotoUseCase(newPhoto)).thenReturn(Result.failure(Exception("Failed to save photo")))

        viewModel.savePhoto(newPhoto)

        val state = viewModel.photos.first { it is PhotosUIState.Error }
        assert((state as PhotosUIState.Error).message == "Failed to save photo")
        verify(savePhotoUseCase).invoke(newPhoto)
    }

    @Test
    fun `test removePhoto success`(): Unit = runBlocking {
        val photo = Photo(
            name = "Dhiessica",
            age = "24",
            date = DateUtil.getTimestampFromDate(),
            fileName = "new_photo.jpg"
        )
        `when`(removePhotoUseCase(context, photo)).thenReturn(Result.success(1))

        viewModel.removePhoto(context, photo)

        val state = viewModel.photos.first { it is PhotosUIState.Success }
        assert(state is PhotosUIState.Success)
        verify(removePhotoUseCase).invoke(context, photo)
    }

    @Test
    fun `test removePhoto failure`(): Unit = runBlocking {
        val photo = Photo(
            name = "Dhiessica",
            age = "24",
            date = DateUtil.getTimestampFromDate(),
            fileName = "new_photo.jpg"
        )

        `when`(removePhotoUseCase(context, photo)).thenReturn(Result.failure(Exception("Error removing photo")))

        viewModel.removePhoto(context, photo)

        val state = viewModel.photos.first { it is PhotosUIState.Error }
        assert((state as PhotosUIState.Error).message == "Error removing photo")
        verify(removePhotoUseCase).invoke(context, photo)
    }

    @Test
    fun `test updateName updates name`() {
        val newName = "Dhiessica P.M"

        viewModel.updateName(newName)

        assert(viewModel.name.value == newName)
    }

    @Test
    fun `test updateAge updates age`() {
        val newAge = "24"

        viewModel.updateAge(newAge)

        assert(viewModel.age.value == newAge)
    }
}
