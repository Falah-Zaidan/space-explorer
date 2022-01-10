package com.example.spaceexplorer.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "APOD")
data class APOD(
    @PrimaryKey
    val id: Long,
    val date: String,
    val explanation: String,
    val hdURL: String

) {
    var favourite: Boolean? = false
    var fetchedFromDjangoService = false
}