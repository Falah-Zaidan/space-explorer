package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
   DjangoMarsRoverApiResponse:
    {
        "count": 1,
        "next": null,
        "previous": null,
        "results": [
            {
                "mars_rover_photo_id": "287024",
                "earth_date": "2021-11-30T08:08:00.083332Z",
                "image_url": "http://mars.nasa.gov/mer/gallery/all/2/f/039/2F129834146EFF04A4P1201R0M1-BR.JPG",
                "rover_name": "Spirit",
                "camera_name": "Front Hazard Avoidance Camera"
            }
        ]
    }
*/

data class DjangoMarsRoverApiResponse(
    @SerializedName("count")
    @Expose
    val count: Int,

    @SerializedName("next")
    @Expose
    val next: String?,

    @SerializedName("previous")
    @Expose
    val previous: String?,

    @SerializedName("results")
    @Expose
    val results: List<MarsRoverPhoto>

) {
    data class MarsRoverPhoto(
        @SerializedName("mars_rover_photo_id")
        @Expose
        val mars_rover_photo_id: Long,

        @SerializedName("earth_date")
        @Expose
        val earth_date: String,

        @SerializedName("image_url")
        @Expose
        val image_url: String,

        @SerializedName("rover_name")
        @Expose
        val rover_name: String,

        @SerializedName("camera_name")
        @Expose
        val camera_name: String
    )
}