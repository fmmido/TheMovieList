package com.smartpixel.themovielist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val genres: String?,
    val runtime: Int?,
    val isFavorite: Boolean,
    val overview: String?
)