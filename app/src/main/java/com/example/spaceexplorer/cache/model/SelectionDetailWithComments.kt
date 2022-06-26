package com.example.spaceexplorer.cache.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.spaceexplorer.model.EditorsPickPhoto

data class SelectionDetailWithComments(

    @Embedded
    val editorsPickPhoto: EditorsPickPhoto,
    @Relation(
        parentColumn = "photoId",
        entityColumn = "editorsPickPhotoId"
    )
    val comments: List<Comment>

)