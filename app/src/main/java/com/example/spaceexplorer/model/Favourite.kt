package com.example.spaceexplorer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class Favourite(
    @PrimaryKey(autoGenerate = true)
    val photo_id: Long,
    val photo_url: String,
    val earth_date: String,
    val rover_name: String,
    val camera_name: String,
    val explanation: String,
    val editorsPickPhotoName: String,
    val editorPickPhotoDateTaken: String,
    val favouriteType: String
)