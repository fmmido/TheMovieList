package com.smartpixel.themovielist.domain.usecase

import androidx.paging.PagingData
import com.smartpixel.themovielist.data.MovieRepository
import com.smartpixel.themovielist.domain.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(coroutineScope: CoroutineScope): Flow<PagingData<Movie>> =
        repository.getMovies(coroutineScope)
}