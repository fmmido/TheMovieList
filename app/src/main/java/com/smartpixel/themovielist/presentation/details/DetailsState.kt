package com.smartpixel.themovielist.presentation.details

import com.smartpixel.themovielist.domain.model.MovieDetails

data class DetailsState(
    val isLoading: Boolean = true,
    val movieDetails: MovieDetails? = null,
    val error: String? = null
)