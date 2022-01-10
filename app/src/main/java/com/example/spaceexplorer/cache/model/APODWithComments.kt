package com.example.spaceexplorer.cache.model

import androidx.room.Embedded
import androidx.room.Relation

data class APODWithComments(

    @Embedded
    val apod: APOD,
    @Relation(
        parentColumn = "id",
        entityColumn = "apodId"
    )
    val comments: List<Comment>

)