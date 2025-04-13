package com.smartpixel.themovielist.presentation.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.domain.model.MovieDetails
import com.smartpixel.themovielist.domain.usecase.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val api: MovieApi,
    private val dao: MovieDao,
    private val database: MovieDatabase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val getMovieDetailsUseCase: GetMovieDetailsUseCase = GetMovieDetailsUseCase(api, dao, database, context)

    private val _movieDetails = MutableStateFlow<MovieDetailsState>(MovieDetailsState.Initial)
    val movieDetails: StateFlow<MovieDetailsState> = _movieDetails

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = MovieDetailsState.Loading
            try {
                val details = getMovieDetailsUseCase(movieId)
                if (details != null) {
                    _movieDetails.value = MovieDetailsState.Success(details)
                } else {
                    _movieDetails.value = MovieDetailsState.Error("Movie details not found")
                }
            } catch (e: Exception) {
                _movieDetails.value = MovieDetailsState.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class MovieDetailsState {
    object Initial : MovieDetailsState()
    object Loading : MovieDetailsState()
    data class Success(val details: MovieDetails) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}