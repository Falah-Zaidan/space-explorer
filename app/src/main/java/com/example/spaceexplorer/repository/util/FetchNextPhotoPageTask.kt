package com.example.spaceexplorer.repository.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spaceexplorer.cache.db.NASADatabase
import com.example.spaceexplorer.cache.model.MarsRoverApiResult
import com.example.spaceexplorer.remote.PhotoService
import com.example.spaceexplorer.util.*
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */

class FetchNextPhotoPageTask constructor(
    private val earth_date: String,
    private val query: String,
    private val photoService: PhotoService,
    private val db: NASADatabase
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val marsRoverApiResultList = db.photoDao().getMarsRoverApiResult()
        val lastListItem = marsRoverApiResultList.takeLast(1).get(0)
        val current = lastListItem
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.next
        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }
        val newValue = try {
            val response = photoService.searchPhotosByDateCall(
                query,
                Constants.nasa_api_key,
                earth_date,
                nextPage
            ).execute()
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    //store new photos into DB
                    //store new 'nextPage' into DB using the value of 'nextPage + 1'
                    val apiResult = MarsRoverApiResult(
                        nextPage + 1
                    )

                    db.runInTransaction {
                        db.photoDao().insertMarsRoverApiResult(apiResult)
                        db.photoDao().insertPhotos(apiResponse.body.marsRoverPhotos)
                    }
                    Resource.success(true)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
