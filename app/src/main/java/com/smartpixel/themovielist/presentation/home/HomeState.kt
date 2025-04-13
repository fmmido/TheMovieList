package com.smartpixel.themovielist.presentation.home

import androidx.paging.PagingData
import com.smartpixel.themovielist.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class HomeState(
    val movies: Flow<PagingData<Movie>> = MutableStateFlow(PagingData.empty()),
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorMessage: String? = null,
    val isGridLayout: Boolean = false
)