package com.smartpixel.themovielist.presentation.home

sealed class HomeIntent {
    data class ToggleFavorite(val movieId: Int, val isFavorite: Boolean) : HomeIntent()
    object ToggleLayout : HomeIntent()
}