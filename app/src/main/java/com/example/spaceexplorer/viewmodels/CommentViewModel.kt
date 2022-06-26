package com.example.spaceexplorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.cache.model.APODWithComments
import com.example.spaceexplorer.cache.model.Comment
import com.example.spaceexplorer.cache.model.SelectionDetailWithComments
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.repository.EditorsPickRepository
import com.example.spaceexplorer.repository.PhotoRepository
import com.example.spaceexplorer.util.Resource
import javax.inject.Inject

@OpenForTesting
class CommentViewModel @Inject constructor(
    private val editorsPickRepository: EditorsPickRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    fun getAPODWithComments(apodId: Long): LiveData<Resource<APODWithComments>> {
        return photoRepository.getAPODComments(apodId)
    }

    fun getSelectionDetailWithComments(selectionDetailId: String): LiveData<Resource<SelectionDetailWithComments>> {
        return editorsPickRepository.getEditorPickComments(selectionDetailId)
    }

    fun insertAPODComment(comment: Comment, apod: APOD) {
        photoRepository.saveAPODComment(comment, apod)
    }

    fun insertEditorsPickPhotoComment(comment: Comment, editorsPickPhoto: EditorsPickPhoto) {
        photoRepository.saveEditorPickComment(comment, editorsPickPhoto)
    }

}
