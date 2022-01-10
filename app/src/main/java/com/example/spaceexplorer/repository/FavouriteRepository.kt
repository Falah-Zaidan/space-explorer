package com.example.spaceexplorer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.DateUtil.Companion.convertFromDate
import com.example.spaceexplorer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random.Default.nextInt

@Singleton
@OpenForTesting
class FavouriteRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val favouriteDao: FavouriteDao
) {

    fun getFavourites(): LiveData<Resource<List<Favourite>>> {

        val mediator = MediatorLiveData<Resource<List<Favourite>>>()

        mediator.addSource(favouriteDao.getFavourites()) {
            if (it != null) {
                mediator.value = Resource.success(it)
            }
        }

        return mediator
    }

    fun deleteFavourite(favourite_photo_id: Long) {
        appExecutors.diskIO().execute {
            favouriteDao.deleteFavourite(favourite_photo_id)
        }
    }

    fun saveMarsRoverPhoto(marsRoverPhoto: MarsRoverPhoto) {
        appExecutors.diskIO().execute {
            favouriteDao.insertFavourite(
                Favourite(
                    marsRoverPhoto.id,
                    marsRoverPhoto.image_href,
                    marsRoverPhoto.earth_date,
                    marsRoverPhoto.rover.name,
                    marsRoverPhoto.camera.name,
                    Constants.no_explanation
                )
            )
        }
    }

    fun saveAPODPhoto(apodPhoto: APOD) {
        appExecutors.diskIO().execute {
            favouriteDao.insertFavourite(
                Favourite(
                    convertFromDate(apodPhoto.date),
                    apodPhoto.hdURL,
                    apodPhoto.date,
                    Constants.abc_rover_name,
                    nextInt().toString(),
                    apodPhoto.explanation
                )
            )
        }
    }

}