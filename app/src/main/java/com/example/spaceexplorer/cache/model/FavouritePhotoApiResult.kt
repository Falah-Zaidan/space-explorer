package com.example.spaceexplorer.cache.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.spaceexplorer.cache.db.Converters

@Entity(tableName = "favourite_photo_api_result", primaryKeys = ["next"])
@TypeConverters(Converters::class)
data class FavouritePhotoApiResult(
    var next: Int?
)
