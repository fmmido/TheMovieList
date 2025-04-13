package com.smartpixel.themovielist.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val id: Int,
    val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val overview: String?,
    val genres: List<Genre>?,
    val runtime: Int?
)

data class Genre(
    val id: Int,
    val name: String
)