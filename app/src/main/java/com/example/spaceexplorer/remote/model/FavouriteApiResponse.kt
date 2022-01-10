package com.example.spaceexplorer.remote.model

import com.example.spaceexplorer.model.Favourite
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
   FavouriteApiResponse:
    {
        "count": 6,
        "next": "http://127.0.0.1:8000/api/favouritephoto/list?page=2",
        "previous": null,
        "results": [
            {
                "photo_id": "333",
                "photo_url": "https://picsum.photos/200/300"
            },
            {
                "photo_id": "1982",
                "photo_url": "https://picsum.photos/200/300"
            },
            {
                "photo_id": "122233",
                "photo_url": "https://picsum.photos/200/300"
            }
        ]
    }
*/

data class FavouriteApiResponse(
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
    val results: List<Favourite>

)