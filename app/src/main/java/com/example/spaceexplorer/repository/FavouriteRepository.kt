package com.example.spaceexplorer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.model.EditorsPickPhoto
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
                    photo_id = marsRoverPhoto.id,
                    photo_url = marsRoverPhoto.image_href,
                    earth_date = marsRoverPhoto.earth_date,
                    rover_name = marsRoverPhoto.rover.name,
                    camera_name = marsRoverPhoto.camera.name,
                    explanation = Constants.no_explanation,
                    favouriteType = "MarsRoverPhoto",
                    editorPickPhotoDateTaken = "",
                    editorsPickPhotoName = ""
                )
            )
        }
    }

    fun saveAPODPhoto(apodPhoto: APOD) {
        appExecutors.diskIO().execute {
            favouriteDao.insertFavourite(
                Favourite(
                    photo_id = convertFromDate(apodPhoto.date),
                    photo_url = apodPhoto.hdURL,
                    earth_date = apodPhoto.date,
                    rover_name = Constants.abc_rover_name,
                    camera_name = nextInt().toString(),
                    explanation = apodPhoto.explanation,
                    favouriteType = "APODPhoto",
                    editorPickPhotoDateTaken = "",
                    editorsPickPhotoName = ""
                )
            )
        }
    }

    fun saveEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        appExecutors.diskIO().execute {
            favouriteDao.insertFavourite(
                Favourite(
                    photo_id = editorsPickPhoto.photoId.toLong(),
                    photo_url = editorsPickPhoto.url,
                    earth_date = "",
                    rover_name = Constants.abc_rover_name,
                    camera_name = nextInt().toString(),
                    explanation = editorsPickPhoto.explanation,
                    favouriteType = "EditorPickPhoto",
                    editorPickPhotoDateTaken = editorsPickPhoto.date,
                    editorsPickPhotoName = editorsPickPhoto.name
                )
            )
        }
    }

}