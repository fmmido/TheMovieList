package com.smartpixel.themovielist.presentation.details

sealed class DetailsEffect {
    data class ShowError(val message: String) : DetailsEffect()
}