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

    private val _trigger: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _editorPickPhotoTrigger: MutableLiveData<String> = MutableLiveData("")

    val editorsPickPhotosLiveData: LiveData<Resource<List<EditorsPickPhoto>>> =
        _trigger.switchMap { _ ->
            editorsPickRepository.getEditorPickPhotosNBR()
        }

    val editorPickPhotoLiveData: LiveData<Resource<EditorsPickPhoto>> =
        _editorPickPhotoTrigger.switchMap { it ->
            editorsPickRepository.getEditorPickPhoto(it)
        }

    fun getEditorPickPhoto(date: String) {
        _editorPickPhotoTrigger.value = date
    }

    fun refreshEditorPickPhotoSingle() {
        _editorPickPhotoTrigger.value?.let {
            _editorPickPhotoTrigger.value = it
        }
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

    fun loadNextPage() {

    }

}
