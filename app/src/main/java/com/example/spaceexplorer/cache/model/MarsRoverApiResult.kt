package com.example.spaceexplorer.cache.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.spaceexplorer.cache.db.Converters

@Entity(tableName = "mars_rover_api_result", primaryKeys = ["next"])
@TypeConverters(Converters::class)
data class MarsRoverApiResult(
    var next: Int?
)

/*
    photos: [
            This is want we want to model here (a single photo)
            -------------
            {
                ...
                id:
                img_src:
                earth_date: YYYY-MM-DD
                ...
            }...
            -------------
        ]
     */
