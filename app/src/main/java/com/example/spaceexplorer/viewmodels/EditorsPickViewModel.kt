package com.example.spaceexplorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.repository.EditorsPickRepository
import com.example.spaceexplorer.repository.FavouriteRepository
import com.example.spaceexplorer.util.Resource
import javax.inject.Inject

@OpenForTesting
class EditorsPickViewModel @Inject constructor(
    private val editorsPickRepository: EditorsPickRepository,
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {

    private val _trigger : MutableLiveData<Boolean> = MutableLiveData(true)

    val editorsPickPhotosLiveData: LiveData<Resource<List<EditorsPickPhoto>>> = _trigger.switchMap { _ ->
        editorsPickRepository.getEditorPickPhotosNBR()
    }

//    var _editorsPickLiveData: LiveData<Resource<List<EditorsPickPhoto>>> =
//        editorsPickRepository.getEditorPickPhotosNBR()

//    var _editorsPickLiveData: LiveData<Resource<List<EditorsPickPhoto>>> =
//        trigger.switchMap {
//            editorsPickRepository.getEditorPickPhotosNBR()
//        }

    fun getEditorPickPhoto(photoId: Long): EditorsPickPhoto {
        editorsPickRepository.getEditorPickPhoto
    }

    fun refresh() {
        _trigger.value?.let {
            _trigger.value = it
        }
    }

    fun setFavouriteEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        favouriteRepository.saveEditorsPickPhoto(editorsPickPhoto)
    }

    fun insertEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        editorsPickRepository.insertEditorsPickPhoto(editorsPickPhoto)
    }

    fun removeEditorsPickPhotoFavourite(editorsPickPhoto: EditorsPickPhoto) {
        favouriteRepository.deleteFavourite(editorsPickPhoto.photoId.toLong())
    }

}
