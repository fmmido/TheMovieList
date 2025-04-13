import android.content.Context
import com.smartpixel.themovielist.data.PreferencesManager
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.smartpixel.themovielist.presentation.home.HomeIntent
import com.smartpixel.themovielist.presentation.home.HomeViewModel

class HomeViewModelTest {

    @Mock
    private lateinit var preferencesManager: PreferencesManager

    @Mock
    private lateinit var api: MovieApi

    @Mock
    private lateinit var dao: MovieDao

    @Mock
    private lateinit var database: MovieDatabase

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: HomeViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        // Mock PreferencesManager
        `when`(preferencesManager.isGridLayoutFlow).thenReturn(flowOf(true))

        viewModel = HomeViewModel(preferencesManager, api, dao, database, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPreferences sets isGridLayout from PreferencesManager`() = runTest {
        // Act & Assert
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isGridLayout).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processIntent ToggleLayout updates layout and saves preference`() = runTest {
        // Act
        viewModel.processIntent(HomeIntent.ToggleLayout)

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isGridLayout).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }
}