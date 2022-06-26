package com.example.spaceexplorer.model

import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
"editor_photo_id": "3232"
"date": "323",
"explanation": "2323",
"url": "3223"
*/

@Entity(
    tableName = "EditorsPickPhoto",
    primaryKeys = ["photoId"]
)
data class EditorsPickPhoto(

    @SerializedName("editor_photo_id")
    @Expose
    val photoId: String,

    @SerializedName("photo_name")
    @Expose
    val name: String,

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
    var favourite: Boolean = false
    var fetchedFromDjangoService = false
}