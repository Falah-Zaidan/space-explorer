package com.example.spaceexplorer.repository

import androidx.lifecycle.LiveData
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.model.Comment
import com.example.spaceexplorer.cache.model.SelectionDetailWithComments
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.remote.DjangoService
import com.example.spaceexplorer.remote.model.DjangoCommentApiResponse
import com.example.spaceexplorer.remote.model.DjangoEditorPickPhotoApiResponseSingle
import com.example.spaceexplorer.remote.model.DjangoEditorsPickResponse
import com.example.spaceexplorer.repository.util.NetworkBoundResource
import com.example.spaceexplorer.util.ApiResponse
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.DateUtil
import com.example.spaceexplorer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class EditorsPickRepository @Inject constructor(
    private val djangoService: DjangoService,
    private val photoDao: PhotoDao,
    private val favouriteDao: FavouriteDao,
    private val appExecutors: AppExecutors
) {

    fun getEditorPickPhotosNBR(): LiveData<Resource<List<EditorsPickPhoto>>> {
        return object :
            NetworkBoundResource<List<EditorsPickPhoto>, DjangoEditorsPickResponse>(appExecutors) {

            override fun saveCallResult(item: DjangoEditorsPickResponse) {
                photoDao.insertEditorPickPhotos(item.results)
            }

            override fun shouldFetch(data: List<EditorsPickPhoto>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<EditorsPickPhoto>> {
                return photoDao.getEditorPickPhotos()
            }

            override fun createCall(): LiveData<ApiResponse<DjangoEditorsPickResponse>> {
                return djangoService.getEditorPickPhotos(authToken = "Token 4d0d38a8857e24501812f9eab292e08426366436")
            }
        }.asLiveData()
    }

//    fun getEditorPickPhoto(date: String): LiveData<Resource<EditorsPickPhoto>> {
//        return object :
//            NetworkBoundResource<EditorsPickPhoto, DjangoEditorPickPhotoApiResponseSingle>(
//                appExecutors
//            ) {
//            override fun saveCallResult(item: DjangoEditorPickPhotoApiResponseSingle) {
//                photoDao.insertEditorPickPhoto(deserialize(item))
//            }
//
//            override fun shouldFetch(data: EditorsPickPhoto?): Boolean {
//                return data == null
//            }
//
//            override fun loadFromDb(): LiveData<EditorsPickPhoto> {
//                return photoDao.getEditorPickPhoto(date)
//            }
//
//            override fun createCall(): LiveData<ApiResponse<DjangoEditorPickPhotoApiResponseSingle>> {
//                return djangoService.getEditorPickPhotoByDate(
//                    authToken = "Token 4d0d38a8857e24501812f9eab292e08426366436",
//                    editor_pick_photo_date = date
//                )
//            }
//        }.asLiveData()
//    }

    fun getEditorPickPhoto(photoId: String): LiveData<Resource<EditorsPickPhoto>> {
        return object :
            NetworkBoundResource<EditorsPickPhoto, DjangoEditorPickPhotoApiResponseSingle>(
                appExecutors
            ) {
            override fun saveCallResult(item: DjangoEditorPickPhotoApiResponseSingle) {
                photoDao.insertEditorPickPhoto(deserialize(item))
            }

            override fun shouldFetch(data: EditorsPickPhoto?): Boolean {
                return true
//                return data == null
            }

            override fun loadFromDb(): LiveData<EditorsPickPhoto> {
                return photoDao.getEditorPickPhoto(photoId)
            }

            override fun createCall(): LiveData<ApiResponse<DjangoEditorPickPhotoApiResponseSingle>> {
                return djangoService.getEditorPickPhotoByDate(
                    authToken = "Token 4d0d38a8857e24501812f9eab292e08426366436",
                    editor_pick_photo_id = photoId
                )
            }
        }.asLiveData()
    }

    private fun deserialize(item: DjangoEditorPickPhotoApiResponseSingle): EditorsPickPhoto {
        val editorPickPhoto = EditorsPickPhoto(
            item.editor_photo_id,
            item.photo_name,
            item.date,
            item.explanation,
            item.url
        )

        return editorPickPhoto
    }

    fun insertEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        appExecutors.diskIO().execute(InsertEditorsPickPhotoRunnable(editorsPickPhoto))
    }

    inner class InsertEditorsPickPhotoRunnable(val editorsPickPhoto: EditorsPickPhoto) : Runnable {
        override fun run() {
            photoDao.insertEditorPickPhoto(editorsPickPhoto)
        }
    }

    fun getEditorPickComments(selectionDetailId: String): LiveData<Resource<SelectionDetailWithComments>> {
        return object :
            NetworkBoundResource<SelectionDetailWithComments, DjangoCommentApiResponse>(appExecutors) {
            override fun saveCallResult(item: DjangoCommentApiResponse) {
                photoDao.insertComments(deserializeDjangoCommentApiResponse(item))
            }

            override fun shouldFetch(data: SelectionDetailWithComments?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<SelectionDetailWithComments> {
                return photoDao.getSelectionDetailWithComments(selectionDetailId.toInt())
            }

            override fun createCall(): LiveData<ApiResponse<DjangoCommentApiResponse>> {
                return djangoService.getSelectionDetailComments(
                    authToken = Constants.adminToken,
                    page = 1,
                    selection_detail_id = selectionDetailId
                )
            }
        }.asLiveData()
    }

    fun deserializeDjangoCommentApiResponse(item: DjangoCommentApiResponse): List<Comment> {
        val comments = arrayListOf<Comment>()

        item.results.forEach {
            comments.add(
                Comment(
                    userCreatorId = it.user_creator_id,
                    apodId = it.apod_id,
                    marsRoverPhotoId = it.mars_rover_id,
                    editorsPickPhotoId = it.editor_photo_id,
                    comment = it.comment_body,
                    dateTime = DateUtil.formatDate(it.date_updated),
                    likes = it.comment_likes,
                    author_name = it.authorName,
                    profile_picture = it.author_profile_pic
                )
            )
        }

        return comments
    }

}