package com.smartpixel.themovielist.domain.usecase

import android.content.Context
import com.smartpixel.themovielist.data.MovieRepository
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.domain.model.MovieDetails
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import com.google.common.truth.Truth.assertThat

class GetMovieDetailsUseCaseTest {

    @Mock
    private lateinit var api: MovieApi

    @Mock
    private lateinit var dao: MovieDao

    @Mock
    private lateinit var database: MovieDatabase

    @Mock
    private lateinit var context: Context

    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetMovieDetailsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = MovieRepository(api, dao, database, context)
        useCase = GetMovieDetailsUseCase(api, dao, database, context)
    }

    @Test
    fun invokeReturnsMovieDetailsWhenRepositorySucceeds() = runTest {
        // Arrange
        val movieId = 123
        val expectedDetails = MovieDetails(
            id = movieId,
            title = "Test Movie",
            overview = "Overview",
            posterPath = "/poster.jpg",
            releaseDate = "2023-01-01",
            genres = "Action",
            runtime = 120
        )
        `when`(repository.getMovieDetails(movieId)).thenReturn(expectedDetails)

        // Act
        val result = useCase(movieId)

        // Assert with detailed checks
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo(expectedDetails.id)
        assertThat(result?.title).isEqualTo(expectedDetails.title)
        assertThat(result?.overview).isEqualTo(expectedDetails.overview)
        assertThat(result?.posterPath).isEqualTo(expectedDetails.posterPath)
        assertThat(result?.releaseDate).isEqualTo(expectedDetails.releaseDate)
        assertThat(result?.genres).isEqualTo(expectedDetails.genres)
        assertThat(result?.runtime).isEqualTo(expectedDetails.runtime)
        assertThat(result).isEqualTo(expectedDetails)  // Final equality check
    }

    @Test
    fun invokeReturnsNullWhenRepositoryFails() = runTest {
        // Arrange
        val movieId = 123
        `when`(repository.getMovieDetails(movieId)).thenReturn(null)

        // Act
        val result = useCase(movieId)

        // Assert
        assertThat(result).isNull()
    }
}