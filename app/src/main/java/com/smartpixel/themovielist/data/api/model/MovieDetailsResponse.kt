package com.smartpixel.themovielist.data.api.model

import com.google.gson.annotations.SerializedName

data class MovieDetailsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("poster") val poster: String?,
    @SerializedName("year") val year: Int?,
    @SerializedName("genre") val genres: List<String>?,
    @SerializedName("runtime") val runtime: Int?
)