package com.example.spaceexplorer.cache.db

/**
 * Defines constants for the Comments Table
 */
object Constants {

    const val PHOTOS_TABLE_NAME = "marsRoverPhotos"

    const val FAVOURITES_TABLE_NAME = "favourite"

    const val QUERY_PHOTOS = "SELECT * FROM" + " " + PHOTOS_TABLE_NAME

    const val DELETE_ALL_PHOTOS = "DELETE FROM" + " " + PHOTOS_TABLE_NAME

    const val DELETE_ALL_FAVOURITES = "DELETE FROM" + " " + FAVOURITES_TABLE_NAME



}