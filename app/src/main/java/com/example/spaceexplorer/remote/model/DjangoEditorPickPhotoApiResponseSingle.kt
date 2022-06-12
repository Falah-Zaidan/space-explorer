package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DjangoEditorPickPhotoApiResponseSingle(
    @SerializedName("editor_photo_id")
    @Expose
    val editor_photo_id: String,

    @SerializedName("date")
    @Expose
    val date: String,

    @SerializedName("explanation")
    @Expose
    val explanation: String,

    @SerializedName("url")
    @Expose
    val url: String,

    @SerializedName("photo_name")
    @Expose
    val photo_name: String

)