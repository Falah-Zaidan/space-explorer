package com.example.spaceexplorer.remote;

import androidx.lifecycle.LiveData
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.remote.model.*
import com.example.spaceexplorer.util.ApiResponse
import retrofit2.Call
import retrofit2.http.*

interface DjangoService {

    @GET("/api/favouritephoto/list")
    fun getFavouriteList(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int
    ): LiveData<ApiResponse<FavouriteApiResponse>>

    @GET("/api/favouritephoto/list")
    fun getFavouriteListCall(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int
    ): Call<FavouriteApiResponse>

    //General
    @GET("/api/favouritephoto/{photo_id}")
    fun getFavourite(
        @Header("Authorization") authToken: String,
        @Path("photo_id") slug: String
    ): LiveData<ApiResponse<Favourite>>

    @FormUrlEncoded
    @POST("/api/favouritephoto/create")
    fun saveFavourite(
        @Header("Authorization") authToken: String,
        @Field("photo_id") photo_id: String, @Field("photo_url") photo_url: String
    ): Call<Favourite>

    @DELETE("/api/favouritephoto/{favourite_photo_id}/delete")
    fun deleteFavourite(
        @Header("Authorization") authToken: String,
        @Path("favourite_photo_id") favourite_photo_id: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/api/marsroverphoto/create")
    fun saveMarsRoverPhoto(
        @Header("Authorization") authToken: String,
        @Field("mars_rover_photo_id") mars_rover_photo_id: String,
        @Field("earth_date") earth_date: String,
        @Field("image_url") image_url: String,
        @Field("rover_name") rover_name: String,
        @Field("camera_name") camera_name: String
    ): Call<MarsRoverPhoto>

    @GET("/api/marsroverphoto/list")
    fun getMarsRoverPhotosCall(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int
    ): Call<DjangoMarsRoverApiResponse>

    //===== APODPhoto related operations =====
    @GET("/api/apodphoto1/list")
    fun getAPODPhoto(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int
    ): Call<DjangoAPODPhotoApiResponse>

    @GET("/api/apodphoto1/{apod_photo_date}")
    fun getAPODPhotoByDate(
        @Header("Authorization") authToken: String,
        @Path("apod_photo_date") apod_photo_date: String
    ): Call<DjangoAPODPhotoApiResponseSingle>

    @FormUrlEncoded
    @POST("/api/apodphoto1/create")
    fun saveAPODPhoto(
        @Header("Authorization") authToken: String,
        @Field("apod_id") apod_id: Long,
        @Field("date") date: String,
        @Field("explanation") explanation: String,
        @Field("url") url: String
    ): Call<DjangoAPODPhotoApiResponse.APODPhoto>

    // == comment related operations == //

    @FormUrlEncoded
    @POST("/api/comments/create")
    fun saveComment(
        @Header("Authorization") authToken: String,
        @Field("comment_id") comment_id: Int,
        @Field("comment_number") comment_number: Int,
        @Field("comment_body") comment_body: String,
        @Field("comment_likes") comment_likes: Int,
        @Field("date_published") date_published: Int,
        @Field("date_updated") date_updated: String,
        @Field("author_name") author_name: String,
        @Field("mars_rover_id") mars_rover_id: Long,
        @Field("apod_id") apod_id: Long,
        @Field("author_profile_pic") author_profile_pic: String,
        @Field("user_creator_id") user_creator_id: Int
    ): Call<DjangoCommentApiResponse.DjangoCommentResponse>

    @DELETE("api/comments/delete/{comment_id}")
    fun deleteComment(
        @Header("Authorization") authToken: String,
        @Path("comment_id") user_creator_id: Int
    ): Call<DjangoDeleteCommentApiResponse>

    @GET("/api/comments/marsroverlist/{mars_rover_id}")
    fun getMarsRoverComments(
        @Header("Authorization") authToken: String,
        @Query("page") page: Int
    ): LiveData<ApiResponse<DjangoCommentApiResponse>>

    @GET("/api/comments/apodlist/{apod_id}")
    fun getAPODComments(
        @Header("Authorization") authToken: String,
        @Path("apod_id") apod_id: Long,
        @Query("page") page: Int
    ): LiveData<ApiResponse<DjangoCommentApiResponse>>

//    @GET("/api/editorpickphoto/list")
//    suspend fun getEditorPickPhotos(
//        @Header("Authorization") authToken: String
//    ): DjangoEditorsPickResponse

    @GET("/api/editorpickphoto/list")
    fun getEditorPickPhotos(
        @Header("Authorization") authToken: String
    ): LiveData<ApiResponse<DjangoEditorsPickResponse>>

    @GET("/api/editorpickphoto/{editor_pick_photo_date}")
    fun getEditorPickPhotoByDate(
        @Header("Authorization") authToken: String,
        @Path("editor_pick_photo_date") editor_pick_photo_date: String
    ): LiveData<ApiResponse<DjangoEditorPickPhotoApiResponseSingle>>
}