package com.example.spaceexplorer.cache.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.spaceexplorer.model.MarsRoverPhoto

data class MarsRoverPhotoWithComments(

    @Embedded
    val mars_photo: MarsRoverPhoto,
    @Relation(
        parentColumn = "id",
        entityColumn = "marsRoverPhotoId"
    )
    val comments: List<Comment>

)