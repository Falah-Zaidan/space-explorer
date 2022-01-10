package com.example.spaceexplorer.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment")
data class Comment(

    @PrimaryKey
    val userCreatorId: Int,
    val apodId: Long,
    val marsRoverPhotoId: Long,

    val comment: String,
    val dateTime: String,
    var likes: Int,
    val author_name: String,
    val profile_picture: String

) {
    var favourite: Boolean? = false
}