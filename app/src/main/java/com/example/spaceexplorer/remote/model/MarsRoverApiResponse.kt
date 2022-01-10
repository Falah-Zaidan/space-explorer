package com.example.spaceexplorer.remote.model

import com.example.spaceexplorer.model.MarsRoverPhoto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MarsRoverApiResponse(
    /*
    List of photos:
        photos: [
            {
                ...
                id:
                img_src:
                earth_date: YYYY-MM-DD
                ..
            }...
        ]
*/
    @SerializedName("photos")
    @Expose
    var marsRoverPhotos: List<MarsRoverPhoto>
)