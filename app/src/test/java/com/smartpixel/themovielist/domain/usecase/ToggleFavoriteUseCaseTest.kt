package com.smartpixel.themovielist.domain.usecase

import android.content.Context
import com.smartpixel.themovielist.data.MovieRepository
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ToggleFavoriteUseCaseTest {

    @Mock
    private lateinit var api: MovieApi

    @Mock
    private lateinit var dao: MovieDao

    @Mock
    private lateinit var database: MovieDatabase

    @Mock
    private lateinit var context: Context

    private lateinit var repository: MovieRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = MovieRepository(api, dao, database, context)
        useCase = ToggleFavoriteUseCase(api, dao, database, context)
    }

    @Test
    fun `invoke calls repository toggleFavorite with correct parameters`() = runTest {
        // Arrange
        val movieId = 123
        val isFavorite = true

        // Act
        useCase(movieId, isFavorite)

        // Assert
        verify(repository).toggleFavorite(movieId, isFavorite)
    }
}