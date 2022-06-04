package com.example.spaceexplorer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.repository.EditorsPickRepository
import com.example.spaceexplorer.repository.FavouriteRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class EditorsPickViewModel @Inject constructor(
    private val editorsPickRepository: EditorsPickRepository,
    private val favouriteRepository: FavouriteRepository
) : ViewModel() {

    var _editorsPickLiveData = MutableLiveData<List<EditorsPickPhoto>>()

    fun getEditorsPicks() {

//        viewModelScope.launch {
//            val editorsPicks = editorsPickRepository.getEditorsPhotos()
//            _editorsPickLiveData.value = editorsPicks
//        }
        editorsPickRepository.getEditorPickPhotosNBR()
    }

    fun refresh() {
        val editorsPickPhoto = _editorsPickLiveData.value
        _editorsPickLiveData.value = editorsPickPhoto
    }

    fun setFavouriteEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        favouriteRepository.saveEditorsPickPhoto(editorsPickPhoto)
    }

}
