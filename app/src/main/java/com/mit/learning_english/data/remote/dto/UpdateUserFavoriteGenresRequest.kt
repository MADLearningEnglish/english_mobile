package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserFavoriteGenresRequest(
    @SerializedName("genreIds") val genreIds: List<Int>
)
