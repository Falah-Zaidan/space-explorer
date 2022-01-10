package com.example.spaceexplorer.remote;

import com.example.spaceexplorer.remote.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface RegisterService {

    @FormUrlEncoded
    @POST("api/account/register")
    fun registerUser(
        @Header("Authorization") authToken: String,
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): Call<RegisterResponse>

}