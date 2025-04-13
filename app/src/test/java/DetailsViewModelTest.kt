package com.smartpixel.themovielist.presentation.details

import android.content.Context
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.domain.model.MovieDetails
import com.smartpixel.themovielist.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.Dispatchers
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

class DetailsViewModelTest {

    @Mock
    private lateinit var api: MovieApi

    @Mock
    private lateinit var dao: MovieDao

    @Mock
    private lateinit var database: MovieDatabase

    @Mock
    private lateinit var context: Context

    private lateinit var useCase: GetMovieDetailsUseCase
    private lateinit var viewModel: DetailsViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        useCase = GetMovieDetailsUseCase(api, dao, database, context)
        viewModel = DetailsViewModel(api, dao, database, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadMovieDetailsEmitsSuccessStateWhenUseCaseReturnsDetails() = runTest {
        // Arrange
        val movieId = 123
        val movieDetails = MovieDetails(
            id = movieId,
            title = "Test Movie",
            overview = "Overview",
            posterPath = "/poster.jpg",
            releaseDate = "2023-01-01",
            genres = "Action",
            runtime = 120
        )
        `when`(useCase(movieId)).thenReturn(movieDetails)

        // Act
        viewModel.loadMovieDetails(movieId)

        // Assert
        viewModel.movieDetails.test {
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Initial)
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Loading)
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Success(movieDetails))
        }
    }

    @Test
    fun loadMovieDetailsEmitsErrorStateWhenUseCaseReturnsNull() = runTest {
        // Arrange
        val movieId = 123
        `when`(useCase(movieId)).thenReturn(null)

        // Act
        viewModel.loadMovieDetails(movieId)

        // Assert
        viewModel.movieDetails.test {
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Initial)
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Loading)
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Error("Movie details not found"))
        }
    }

    @Test
    fun loadMovieDetailsEmitsErrorStateWhenUseCaseThrowsException() = runTest {
        // Arrange
        val movieId = 123
        `when`(useCase(movieId)).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.loadMovieDetails(movieId)

        // Assert
        viewModel.movieDetails.test {
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Initial)
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Loading)
            assertThat(awaitItem()).isEqualTo(MovieDetailsState.Error("Network error"))
        }
    }
}