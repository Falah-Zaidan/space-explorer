package com.example.spaceexplorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.repository.EditorsPickRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class EditorsPickViewModel @Inject constructor(
    private val editorsPickRepository: EditorsPickRepository
) : ViewModel() {

    var _editorsPickLiveData = MutableLiveData<List<EditorsPickPhoto>>()

    fun getEditorsPicks() {

        viewModelScope.launch {
            val editorsPicks = editorsPickRepository.getEditorsPhotos()
            _editorsPickLiveData.value = editorsPicks
        }

    }

}
