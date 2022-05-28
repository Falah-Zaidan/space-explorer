package com.example.spaceexplorer.repository

import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.remote.DjangoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class EditorsPickRepository @Inject constructor(
    private val service: DjangoService
) {

    suspend fun getEditorsPhotos(): List<EditorsPickPhoto>? {

        return service.getEditorPickPhotos(
            authToken = "Token 4d0d38a8857e24501812f9eab292e08426366436"
        ).results

//        var editorsPickPhotoList: List<EditorsPickPhoto>? = listOf<EditorsPickPhoto>()

//        serviceCall.enqueue(object : Callback<DjangoEditorsPickResponse> {
//            override fun onResponse(
//                call: Call<DjangoEditorsPickResponse>,
//                response: Response<DjangoEditorsPickResponse>
//            ) {
//                editorsPickPhotoList = response.body()?.results
//            }
//
//            override fun onFailure(call: Call<DjangoEditorsPickResponse>, t: Throwable) {
//
//            }
//        })

    }

}