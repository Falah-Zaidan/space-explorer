package com.example.spaceexplorer.remote;

import com.example.spaceexplorer.remote.model.APIUser
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {

    @FormUrlEncoded
    @POST("api-token-auth/")
    fun login(
        @Field("username") email: String, @Field("password") password: String
    ): Call<APIUser>

}