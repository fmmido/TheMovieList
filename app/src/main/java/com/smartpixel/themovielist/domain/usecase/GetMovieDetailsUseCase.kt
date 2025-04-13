package com.smartpixel.themovielist.domain.usecase

import android.content.Context
import com.smartpixel.themovielist.data.MovieRepository
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.domain.model.MovieDetails
import dagger.hilt.android.qualifiers.ApplicationContext

class GetMovieDetailsUseCase(
    api: MovieApi,
    dao: MovieDao,
    database: MovieDatabase,
    @ApplicationContext context: Context
) {
    private val repository: MovieRepository = MovieRepository(api, dao, database, context)

    suspend operator fun invoke(movieId: Int): MovieDetails? {
        return repository.getMovieDetails(movieId)
    }
}