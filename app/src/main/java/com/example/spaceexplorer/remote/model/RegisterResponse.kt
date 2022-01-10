package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
    a successful response (created user)
   {
        "response": "successfully registered a new user.",
        "email": "some@hotmail.com",
        "username": "something",
        "token": "26df629776c3fd761698f0259fd1665eced64617"
    }
*/

data class RegisterResponse(
    @SerializedName("response")
    @Expose
    val response: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("username")
    @Expose
    val username: String,

    @SerializedName("token")
    @Expose
    val token: String

)