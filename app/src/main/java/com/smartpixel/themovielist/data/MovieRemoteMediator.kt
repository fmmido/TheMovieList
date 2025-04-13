package com.smartpixel.themovielist.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.smartpixel.themovielist.data.mapper.toEntity
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.data.local.entity.MovieEntity
import com.smartpixel.themovielist.data.room.RemoteKey
import com.smartpixel.themovielist.data.room.RemoteKeyDao
import dagger.hilt.android.qualifiers.ApplicationContext

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val api: MovieApi,
    private val dao: MovieDao,
    private val remoteKeyDao: RemoteKeyDao,
    @ApplicationContext private val context: Context
) : RemoteMediator<Int, MovieEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        if (!isOnline()) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.remoteKeyByQuery("movies")
                    if (remoteKey?.nextPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextPage
                }
            }

            val response = api.getMovies(page)
            val movies = response.results
            val endOfPaginationReached = page >= response.total_pages

            if (loadType == LoadType.REFRESH) {
                dao.clearMovies()
                remoteKeyDao.deleteByQuery("movies")
            }
            val entities = movies.map { it.toEntity(dao.getMovieById(it.id)?.isFavorite ?: false) }
            dao.insertMovies(entities)
            remoteKeyDao.insertOrReplace(
                RemoteKey(
                    query = "movies",
                    nextPage = if (endOfPaginationReached) null else page + 1
                )
            )

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}