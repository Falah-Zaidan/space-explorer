package com.example.spaceexplorer.repository

import androidx.lifecycle.LiveData
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.remote.DjangoService
import com.example.spaceexplorer.remote.model.DjangoEditorsPickResponse
import com.example.spaceexplorer.repository.util.NetworkBoundResource
import com.example.spaceexplorer.util.ApiResponse
import com.example.spaceexplorer.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class EditorsPickRepository @Inject constructor(
    private val service: DjangoService,
    private val photoDao: PhotoDao,
    private val favouriteDao: FavouriteDao,
    private val appExecutors: AppExecutors
) {

//    suspend fun getEditorsPhotos(): List<EditorsPickPhoto>? {
//
//        return service.getEditorPickPhotos(
//            authToken = "Token 4d0d38a8857e24501812f9eab292e08426366436"
//        ).results
//
////        var editorsPickPhotoList: List<EditorsPickPhoto>? = listOf<EditorsPickPhoto>()
//
////        serviceCall.enqueue(object : Callback<DjangoEditorsPickResponse> {
////            override fun onResponse(
////                call: Call<DjangoEditorsPickResponse>,
////                response: Response<DjangoEditorsPickResponse>
////            ) {
////                editorsPickPhotoList = response.body()?.results
////            }
////
////            override fun onFailure(call: Call<DjangoEditorsPickResponse>, t: Throwable) {
////
////            }
////        })
//
//    }

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
                return service.getEditorPickPhotos(authToken = "Token 4d0d38a8857e24501812f9eab292e08426366436")
            }
        }.asLiveData()
    }

    fun getEditorPickPhoto(): LiveData<Resource<EditorsPickPhoto>> {
        return object : NetworkBoundResource<EditorsPickPhoto, >(appExecutors) {

        }
    }

    fun insertEditorsPickPhoto(editorsPickPhoto: EditorsPickPhoto) {
        appExecutors.diskIO().execute(InsertEditorsPickPhotoRunnable(editorsPickPhoto))
    }

    inner class InsertEditorsPickPhotoRunnable(val editorsPickPhoto: EditorsPickPhoto) : Runnable {
        override fun run() {
            photoDao.insertEditorPickPhoto(editorsPickPhoto)
        }
    }

}