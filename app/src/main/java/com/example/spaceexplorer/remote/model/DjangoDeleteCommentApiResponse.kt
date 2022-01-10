package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DjangoDeleteCommentApiResponse(
    @SerializedName("success")
    @Expose
    val responseMessage: String
)