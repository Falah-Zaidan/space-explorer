package com.example.spaceexplorer.remote.model

import com.example.spaceexplorer.model.EditorsPickPhoto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
   EditorsPickResponse:
   {
    "count": 1,
    "next": null,
    "previous": null,
    "results": [
        {
            "editor_photo_id": "3232",
            "date": "323",
            "explanation": "2323",
            "url": "3223"
        }
    ]
}
*/
data class DjangoEditorsPickResponse(

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
    val results: List<EditorsPickPhoto>
)