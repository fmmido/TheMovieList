package com.smartpixel.themovielist.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("discover/movie")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = "4e4a85485f7772ec816ce08948f738b3"
    ): MovieListResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = "4e4a85485f7772ec816ce08948f738b3"
    ): MovieResponse
}

data class MovieListResponse(
    val page: Int,
    val results: List<MovieResponse>,
    val total_pages: Int,
    val total_results: Int
)

data class MovieResponse(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val release_date: String?,
    val overview: String?,
    val genres: List<Genre>?,
    val runtime: Int?
)

data class Genre(
    val id: Int,
    val name: String
)