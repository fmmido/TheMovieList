package com.smartpixel.themovielist.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.map
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.api.MovieResponse
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.data.local.entity.MovieEntity
import com.smartpixel.themovielist.data.mapper.toDetails
import com.smartpixel.themovielist.data.mapper.toDomainModel
import com.smartpixel.themovielist.data.mapper.toEntity
import com.smartpixel.themovielist.data.mapper.toMovieDetails
import com.smartpixel.themovielist.domain.model.Movie
import com.smartpixel.themovielist.domain.model.MovieDetails
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepository(
    private val api: MovieApi,
    private val dao: MovieDao,
    private val database: MovieDatabase,
    @ApplicationContext private val context: Context
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getMovies(coroutineScope: CoroutineScope): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = MovieRemoteMediator(api, dao, database.remoteKeyDao(), context),
            pagingSourceFactory = { dao.getMovies() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomainModel() }
        }.cachedIn(coroutineScope)
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetails? {
        val cachedMovie = dao.getMovieById(movieId)
        if (cachedMovie != null && !isOnline()) {
            return cachedMovie.toDetails()
        }

        if (isOnline()) {
            try {
                val movie = api.getMovieDetails(movieId)
                val entity = movie.toEntity(cachedMovie?.isFavorite ?: false)
                dao.insertMovies(listOf(entity))
                return movie.toMovieDetails()
            } catch (e: Exception) {
                return cachedMovie?.toDetails()
            }
        }
        return null
    }

    suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
        val movie = dao.getMovieById(movieId)
        if (movie != null) {
            dao.updateMovie(movie.copy(isFavorite = isFavorite))
        }
    }

    fun getCachedMovies(): Flow<List<Movie>> {
        return dao.getCachedMovies().map { entities ->
            entities.map { entity -> entity.toDomainModel() }
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

class MoviePagingSource(
    private val api: MovieApi,
    private val dao: MovieDao
) : PagingSource<Int, MovieResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResponse> {
        return try {
            val page = params.key ?: 1
            val response = api.getMovies(page)
            val movies = response.results
            val entities = movies.map { it.toEntity(dao.getMovieById(it.id)?.isFavorite ?: false) }
            dao.insertMovies(entities)
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.total_pages) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieResponse>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}