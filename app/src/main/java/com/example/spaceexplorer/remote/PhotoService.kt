package com.example.spaceexplorer.remote;

import androidx.lifecycle.LiveData
import com.example.spaceexplorer.model.APODPhoto
import com.example.spaceexplorer.remote.model.MarsRoverApiResponse
import com.example.spaceexplorer.util.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoService {

    @GET("/mars-photos/api/v1/rovers/{rover_name}/photos")
    fun searchPhotosByDate(
        @Path("rover_name") name: String,
        @Query("api_key") api_key: String?,
        @Query("earth_date") earth_date: String?,
        @Query("page") page: Int
    ): LiveData<ApiResponse<MarsRoverApiResponse>>

    @GET("/mars-photos/api/v1/rovers/{rover_name}/photos")
    fun searchPhotosByDateCall(
        @Path("rover_name") name: String,
        @Query("api_key") api_key: String?,
        @Query("earth_date") query: String?,
        @Query("page") page: Int
    ): Call<MarsRoverApiResponse>

    @GET("/planetary/apod")
    fun getAstronomyPictureOfTheDay(
        @Query("date") date: String,
        @Query("api_key") api_key: String?
    ): LiveData<ApiResponse<APODPhoto>>
}