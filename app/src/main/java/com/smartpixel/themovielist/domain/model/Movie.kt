package com.smartpixel.themovielist.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String? = null,
    val genres: List<String> = emptyList(),
    val runtime: String? = null,
    val isFavorite: Boolean
)