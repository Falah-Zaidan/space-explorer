package com.example.spaceexplorer.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spaceexplorer.cache.db.Constants
import com.example.spaceexplorer.cache.model.FavouritePhotoApiResult
import com.example.spaceexplorer.model.Favourite

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourite")
    fun getFavourites(): LiveData<List<Favourite>>

    @Query("DELETE FROM favourite WHERE favourite.photo_id like :favourite_id")
    fun deleteFavourite(favourite_id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavouriteList(favouriteList: List<Favourite>)

    //this is used for pagination
    @Query("SELECT * FROM favourite_photo_api_result")
    fun getFavouriteApiResult(): List<FavouritePhotoApiResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteApiResult(result: FavouritePhotoApiResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavourites(favouriteList: List<Favourite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavourite(favourite: Favourite)

    @Query(Constants.DELETE_ALL_FAVOURITES)
    suspend fun clearPhotos()

}

