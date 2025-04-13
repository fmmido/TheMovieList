package com.smartpixel.themovielist.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.data.local.entity.MovieEntity
import com.smartpixel.themovielist.domain.model.MovieDetails
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MovieRepositoryTest {

    @Mock
    private lateinit var api: MovieApi

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Mock
    private lateinit var network: android.net.Network

    @Mock
    private lateinit var networkCapabilities: NetworkCapabilities

    private lateinit var dao: MovieDao
    private lateinit var database: MovieDatabase
    private lateinit var repository: MovieRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        database = Room.inMemoryDatabaseBuilder(
            context, MovieDatabase::class.java
        ).build()
        dao = database.movieDao()

        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)

        repository = MovieRepository(api, dao, database, context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getMovieDetailsReturnsCachedDataWhenOffline() = runTest {
        // Arrange
        val movieId = 123
        val cachedMovie = MovieEntity(
            id = movieId,
            title = "Cached Movie",
            posterPath = "/cached.jpg",
            releaseDate = "2023-01-01",
            overview = "Cached Overview",
            genres = "Action",
            runtime = 120,
            isFavorite = false
        )
        dao.insertMovies(listOf(cachedMovie))
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)

        // Act
        val result = repository.getMovieDetails(movieId)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result?.title).isEqualTo("Cached Movie")
    }

    @Test
    fun getMovieDetailsReturnsNullWhenOnlineAndNoCachedData() = runTest {
        // Arrange
        val movieId = 123
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(dao.getMovieById(movieId)).thenReturn(null)
        `when`(api.getMovieDetails(movieId)).thenThrow(RuntimeException("API error"))

        // Act
        val result = repository.getMovieDetails(movieId)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun toggleFavoriteUpdatesFavoriteStateInDatabase() = runTest {
        // Arrange
        val movieId = 123
        val movie = MovieEntity(
            id = movieId,
            title = "Test Movie",
            posterPath = "/test.jpg",
            releaseDate = "2023-01-01",
            overview = "Test Overview",
            genres = "",
            runtime = 120,
            isFavorite = false
        )
        dao.insertMovies(listOf(movie))

        // Act
        repository.toggleFavorite(movieId, true)

        // Assert
        val updatedMovie = dao.getMovieById(movieId)
        assertThat(updatedMovie?.isFavorite).isTrue()
    }
}