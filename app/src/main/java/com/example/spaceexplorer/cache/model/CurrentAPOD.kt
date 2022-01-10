package com.example.spaceexplorer.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CurrentAPOD")
data class CurrentAPOD(
    @PrimaryKey
    val date: String
)