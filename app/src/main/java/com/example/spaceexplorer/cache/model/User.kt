package com.example.spaceexplorer.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(

    @PrimaryKey
    var id: Int,
    var name: String,
    var profilePictureUrl: String
) {
    var authToken: String? = null
}