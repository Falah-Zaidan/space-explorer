package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//{
//    "count": 1,
//    "next": null,
//    "previous": null,
//    "results": [
//        {
//            "comment_id": "47",
//            "comment_number": "33",
//            "comment_body": "new comment",
//            "comment_likes": "15",
//            "date_updated": "2021-12-10T08:15:43.193394Z",
//            "author_name": "abde12345",
//            "apod_photo": null,
//            "mars_rover_photo": null,
//            "apod_id": "-1",
//            "mars_rover_id": "89"
//        }
//    ]
//}

data class DjangoCommentApiResponse(
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
    val results: List<DjangoCommentResponse>
) {
    data class DjangoCommentResponse(
        @SerializedName("comment_number")
        @Expose
        val comment_number: Int,

        @SerializedName("comment_body")
        @Expose
        val comment_body: String,

        @SerializedName("comment_likes")
        @Expose
        val comment_likes: Int,

        @SerializedName("date_updated")
        @Expose
        val date_updated: String,

        @SerializedName("author_name")
        @Expose
        val authorName: String,

        @SerializedName("apod_id")
        @Expose
        val apod_id: Long,

        @SerializedName("mars_rover_id")
        @Expose
        val mars_rover_id: Long,

        @SerializedName("author_profile_pic")
        @Expose
        val author_profile_pic: String,

        @SerializedName("user_creator_id")
        @Expose
        val user_creator_id: Int
    )
}
