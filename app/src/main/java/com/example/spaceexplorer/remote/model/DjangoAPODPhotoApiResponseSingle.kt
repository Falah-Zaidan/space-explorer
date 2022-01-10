package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
   DjangoMarsRoverApiResponse:
   {
        "apod_id": "4445",
        "date": "2021-12-10",
        "explanation": "fasdfasd",
        "url": "fadsfsda",
        "comments": []
    }
*/
data class DjangoAPODPhotoApiResponseSingle(
    @SerializedName("apod_id")
    @Expose
    val apod_id: Int,

    @SerializedName("date")
    @Expose
    val date: String,

    @SerializedName("explanation")
    @Expose
    val explanation: String,

    @SerializedName("url")
    @Expose
    val image_url: String,

    //list of comments
    @SerializedName("comments")
    @Expose
    val comments: List<Comment?>
) {

    data class Comment(
        @SerializedName("comment_id")
        @Expose
        val comment_id: Int,

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

        @SerializedName("author")
        @Expose
        val author: String
    )
}