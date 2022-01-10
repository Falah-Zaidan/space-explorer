package com.example.spaceexplorer.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.dao.LoginDao
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.model.*
import com.example.spaceexplorer.model.APODPhoto
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto

@Database(
    entities = [MarsRoverPhoto::class, MarsRoverApiResult::class, APODPhoto::class, Favourite::class, Comment::class, User::class, APOD::class, FavouritePhotoApiResult::class, CurrentAPOD::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NASADatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    abstract fun favouriteDao(): FavouriteDao

    abstract fun loginDao(): LoginDao

}
