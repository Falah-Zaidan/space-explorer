package com.example.spaceexplorer.model

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "marsRoverPhotos",
    primaryKeys = ["id", "rover_name"]
)
data class MarsRoverPhoto(
    @SerializedName("id")
    @Expose
    val id: Long,
    @SerializedName("img_src")
    @Expose
    val image_href: String,
    @SerializedName("earth_date")
    @Expose
    val earth_date: String,

    @SerializedName("rover")
    @Expose
    @Embedded(prefix = "rover_")
    val rover: Rover,

    @SerializedName("camera")
    @Expose
    @Embedded
    val camera: Camera
) {
    var favourite: Boolean? = false

    data class Rover(
        @SerializedName("name")
        @Expose
        val name: String
    )

    data class Camera(
        @SerializedName("full_name")
        @Expose
        val name: String
    )
}

/*
    photos: [
            This is want we want to model here (a single photo)
            -------------
            {
                ...
                id:
                img_src:
                earth_date: YYYY-MM-DD
                ...
            }...
            -------------
        ]
     */
