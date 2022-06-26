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
            "apod_id": "33",
            "date": "2021-12-07T07:36:18.904973Z",
            "explanation": "sdlfmaklsfjld",
            "url": "https://picsum.photos/200/300",
            "comments": [
                {
                    "comment_id": "333",
                    "comment_number": "44",
                    "comment_body": "sdlfjkajfldsf",
                    "comment_likes": "4",
                    "date_updated": "2021-12-07T08:14:14.891318Z",
                    "author": 1
                },...
*/

data class DjangoEditorsPickPhotoApiResponse(
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
    val results: List<EditorPickPhoto>

) {
    data class EditorPickPhoto(
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
    )

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