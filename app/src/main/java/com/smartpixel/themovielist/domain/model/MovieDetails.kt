package com.smartpixel.themovielist.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String? = null, // Made optional with default null
    val genres: String?, // Made optional with default empty list
    val runtime: Int? // Already nullable, added default null
)