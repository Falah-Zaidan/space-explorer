package com.example.spaceexplorer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "APODPhotos")
data class APODPhoto(
    @PrimaryKey
    @SerializedName("date")
    @Expose
    val date: String,

    @SerializedName("explanation")
    @Expose
    val explanation: String,

    @SerializedName("url")
    @Expose
    val url: String

) {
    var favourite: Boolean? = false
    var fetchedFromDjangoService = false
}