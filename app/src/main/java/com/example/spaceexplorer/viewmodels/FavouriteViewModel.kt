package com.example.spaceexplorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.repository.EditorsPickRepository
import com.example.spaceexplorer.repository.FavouriteRepository
import com.example.spaceexplorer.repository.PhotoRepository
import com.example.spaceexplorer.util.Resource
import javax.inject.Inject

@OpenForTesting
class FavouriteViewModel @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val photoRepository: PhotoRepository,
    private val editorsPickRepository: EditorsPickRepository
) : ViewModel() {

    fun getFavourites(): LiveData<Resource<List<Favourite>>> {
        return favouriteRepository.getFavourites()
    }

    fun deleteFavourite(favourite_photo_id: Long) {
        favouriteRepository.deleteFavourite(favourite_photo_id)
    }

    fun insertMarsRoverPhoto(photo: MarsRoverPhoto) {
        photoRepository.insertMarsRoverPhoto(photo)
    }

    fun insertAPOD(apod: APOD) {
        photoRepository.insertAPOD(apod)
    }

    fun insertEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        editorsPickRepository.insertEditorsPickPhoto(editorsPickPhoto)
    }
}
