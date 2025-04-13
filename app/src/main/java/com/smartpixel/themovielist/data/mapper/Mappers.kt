package com.smartpixel.themovielist.data.mapper

import com.smartpixel.themovielist.data.api.MovieResponse
import com.smartpixel.themovielist.data.local.entity.MovieEntity
import com.smartpixel.themovielist.domain.model.Movie
import com.smartpixel.themovielist.domain.model.MovieDetails

const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

fun MovieResponse.toEntity(isFavorite: Boolean): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        posterPath = poster_path?.let { TMDB_IMAGE_BASE_URL + it },
        releaseDate = release_date,
        overview = overview,
        genres = genres?.joinToString(", ") { it.name },
        runtime = runtime,
        isFavorite = isFavorite
    )
}

fun MovieResponse.toDomainModel(isFavorite: Boolean): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = poster_path?.let { TMDB_IMAGE_BASE_URL + it },
        releaseDate = release_date,
        isFavorite = isFavorite
    )
}

fun MovieResponse.toMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        overview = overview,
        posterPath = poster_path?.let { TMDB_IMAGE_BASE_URL + it },
        releaseDate = release_date,
        genres = genres?.joinToString(", ") { it.name },
        runtime = runtime
    )
}

fun MovieEntity.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        isFavorite = isFavorite
    )
}

fun MovieEntity.toDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        genres = genres,
        runtime = runtime
    )
}