package com.smartpixel.themovielist.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.smartpixel.themovielist.data.MovieRepository
import com.smartpixel.themovielist.data.PreferencesManager
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.domain.model.Movie
import com.smartpixel.themovielist.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val api: MovieApi,
    private val dao: MovieDao,
    private val database: MovieDatabase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val repository: MovieRepository = MovieRepository(api, dao, database, context)
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = ToggleFavoriteUseCase(api, dao, database, context)

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadPreferences()
        loadMovies()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val isGridLayout = preferencesManager.isGridLayoutFlow.first()
            _state.value = _state.value.copy(isGridLayout = isGridLayout)
        }
    }

    private fun loadMovies() {
        val moviesFlow: Flow<PagingData<Movie>> = repository.getMovies(viewModelScope)
            .cachedIn(viewModelScope)  // Cache the PagingData to prevent reloading
        _state.value = _state.value.copy(movies = moviesFlow)
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        toggleFavoriteUseCase(intent.movieId, intent.isFavorite)
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            errorMessage = "Failed to toggle favorite: ${e.message}"
                        )
                    }
                }
            }
            is HomeIntent.ToggleLayout -> {
                val newLayout = !_state.value.isGridLayout
                _state.value = _state.value.copy(isGridLayout = newLayout)
                viewModelScope.launch {
                    preferencesManager.setGridLayout(newLayout)
                }
            }
        }
    }

    fun clearErrorMessage() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}
