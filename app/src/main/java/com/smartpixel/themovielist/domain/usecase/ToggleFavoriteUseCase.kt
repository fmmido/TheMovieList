package com.smartpixel.themovielist.domain.usecase

import com.smartpixel.themovielist.data.MovieRepository
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context

class ToggleFavoriteUseCase(
    api: MovieApi,
    dao: MovieDao,
    database: MovieDatabase,
    @ApplicationContext context: Context
) {
    private val repository: MovieRepository = MovieRepository(api, dao, database, context)

    suspend operator fun invoke(movieId: Int, isFavorite: Boolean) = repository.toggleFavorite(movieId, isFavorite)
}