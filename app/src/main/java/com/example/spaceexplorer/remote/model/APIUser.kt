package com.example.spaceexplorer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class APIUser(

    @SerializedName("token")
    @Expose
    val authToken: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("user_name")
    @Expose
    val user_name: String,

    @SerializedName("user_id")
    @Expose
    val user_id: Int

)
