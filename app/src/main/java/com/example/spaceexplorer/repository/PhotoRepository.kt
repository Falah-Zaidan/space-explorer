package com.example.spaceexplorer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.db.NASADatabase
import com.example.spaceexplorer.cache.model.*
import com.example.spaceexplorer.model.APODPhoto
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.remote.DjangoService
import com.example.spaceexplorer.remote.PhotoService
import com.example.spaceexplorer.remote.model.*
import com.example.spaceexplorer.repository.util.FetchNextPhotoPageTask
import com.example.spaceexplorer.repository.util.NetworkBoundResource
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.util.DateUtil.Companion.convertFromDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random.Default.nextInt

@Singleton
@OpenForTesting
class PhotoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: NASADatabase,
    private val photoDao: PhotoDao,
    private val photoService: PhotoService,
    private val djangoService: DjangoService
) {

    fun loadPhotoById(id: Long): LiveData<Resource<MarsRoverPhoto>> {
        val mediator = MediatorLiveData<Resource<MarsRoverPhoto>>()

        mediator.addSource(photoDao.getPhoto(id), Observer { photo ->
            mediator.value = Resource.success(photo)
        })

        return mediator
    }

    fun loadMarsRoverPhotos(
        rover_name: String,
        earth_date: String
    ): LiveData<Resource<List<MarsRoverPhoto>>> { //page: Int parameter
        return object :
            NetworkBoundResource<List<MarsRoverPhoto>, MarsRoverApiResponse>(appExecutors) {
            override fun saveCallResult(item: MarsRoverApiResponse) {

                val apiResult = MarsRoverApiResult(
                    2
                )

                photoDao.insertMarsRoverApiResult(apiResult)

                val photos = item.marsRoverPhotos
                photoDao.insertPhotos(photos)
            }

            override fun shouldFetch(data: List<MarsRoverPhoto>?): Boolean {
                return true
            }

            override fun loadFromDb() = photoDao.getPhotosByRoverNameAndDate(rover_name, earth_date)

            //query from django service first, if not available, query from photoService (NASA api)
            override fun createCall(): LiveData<ApiResponse<MarsRoverApiResponse>> {

                val marsRoverPhotos = arrayListOf<MarsRoverPhoto>()

                //query django service
                //only getting the first page - implement similar pagination to the MarsRoverPhoto back-end - so they sync
                val djangoServiceResponse = djangoService.getMarsRoverPhotosCall(
                    Constants.adminToken,
                    1
                )

                djangoServiceResponse.enqueue(object : Callback<DjangoMarsRoverApiResponse> {
                    override fun onResponse(
                        call: Call<DjangoMarsRoverApiResponse>,
                        response: Response<DjangoMarsRoverApiResponse>
                    ) {
                        response.body()?.results?.forEach {
                            marsRoverPhotos.add(convertFromDjangoMarsRoverPhoto(it))
                        }
                    }

                    override fun onFailure(call: Call<DjangoMarsRoverApiResponse>, t: Throwable) {
                    }
                })

                val mediatorLiveData = MediatorLiveData<ApiResponse<MarsRoverApiResponse>>()

                mediatorLiveData.addSource(
                    photoService.searchPhotosByDate(
                        rover_name, Constants.nasa_api_key, earth_date, 1
                    )
                ) {

                    when (it) {
                        is ApiSuccessResponse -> {
                            //insert all photos into list with id's that are not currently contained in the list already
                            it.body.marsRoverPhotos.forEach { marsRoverPhoto ->
                                if (!checkContainedInList(marsRoverPhotos, marsRoverPhoto.id)) {
                                    //if it's not contained in the list, add the item to the list
                                    marsRoverPhotos.add(marsRoverPhoto)
                                }
                                //if it's contained in the list, do nothing
                            }
                        }
                        is ApiErrorResponse -> {
                        }
                        is ApiEmptyResponse -> {
                        }
                    }

                    mediatorLiveData.value = ApiResponse.create(
                        Response.success(
                            MarsRoverApiResponse(
                                marsRoverPhotos
                            )
                        )
                    )
                }

                return mediatorLiveData

            }

            override fun onFetchFailed() {
            }

        }.asLiveData()
    }

    private fun checkContainedInList(
        marsRoverPhotos: List<MarsRoverPhoto>,
        id: Long
    ): Boolean {

        marsRoverPhotos.forEach {
            if (it.id == id) {
                return true
            }
        }

        return false
    }

    private fun convertFromDjangoMarsRoverPhoto(djangoMarsRoverPhoto: DjangoMarsRoverApiResponse.MarsRoverPhoto): MarsRoverPhoto {
        return MarsRoverPhoto(
            djangoMarsRoverPhoto.mars_rover_photo_id,
            djangoMarsRoverPhoto.image_url,
            djangoMarsRoverPhoto.earth_date,
            MarsRoverPhoto.Rover(djangoMarsRoverPhoto.rover_name),
            MarsRoverPhoto.Camera(djangoMarsRoverPhoto.camera_name)
        )
    }

    fun loadAPOD(date: String): LiveData<Resource<APOD>> { //page: Int parameter
        return object : NetworkBoundResource<APOD, APODPhoto>(appExecutors) {
            override fun saveCallResult(item: APODPhoto) {
                photoDao.insertAPOD(deserialize(item))
            }

            override fun shouldFetch(data: APOD?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<APOD> {
                return photoDao.getAPOD(date)
            }

            override fun createCall(): LiveData<ApiResponse<APODPhoto>> {

                var apodPhoto = APODPhoto("", "", "")

                val djangoServiceResponse = djangoService.getAPODPhotoByDate(
                    Constants.adminToken,
                    date
                )

                djangoServiceResponse.enqueue(object : Callback<DjangoAPODPhotoApiResponseSingle> {

                    override fun onResponse(
                        call: Call<DjangoAPODPhotoApiResponseSingle>,
                        response: Response<DjangoAPODPhotoApiResponseSingle>
                    ) {
                        if (response.body() != null) {
                            //set within a callback - must be posted to a liveData that is observed...
                            apodPhoto = convertFromDjangoAPODPhoto(response.body()!!)

                            apodPhoto.fetchedFromDjangoService = true
                        }
                    }

                    override fun onFailure(
                        call: Call<DjangoAPODPhotoApiResponseSingle>,
                        t: Throwable
                    ) {
                    }
                })

                val mediatorLiveData = MediatorLiveData<ApiResponse<APODPhoto>>()

                mediatorLiveData.addSource(
                    photoService.getAstronomyPictureOfTheDay(
                        date,
                        Constants.nasa_api_key
                    )
                ) {
                    //check to see if contained
                    when (it) {
                        is ApiSuccessResponse -> {
                            if (apodPhoto.date.isEmpty()) {
                                apodPhoto = it.body
                            }
                        }
                        is ApiErrorResponse -> {
                        }
                        is ApiEmptyResponse -> {
                        }
                    }

                    mediatorLiveData.value = ApiResponse.create(
                        Response.success(
                            apodPhoto
                        )
                    )
                }

                return mediatorLiveData
            }
        }.asLiveData()
    }

    private fun convertFromDjangoAPODPhoto(djangoAPODPhotoApiResponseSingle: DjangoAPODPhotoApiResponseSingle): APODPhoto {
        return APODPhoto(
            djangoAPODPhotoApiResponseSingle.date,
            djangoAPODPhotoApiResponseSingle.explanation,
            djangoAPODPhotoApiResponseSingle.image_url
        )
    }

    fun insertCurrentAPOD(currentAPOD: CurrentAPOD) {
        appExecutors.diskIO().execute {
            photoDao.deleteAPODEntries()
            photoDao.insertCurrentAPOD(currentAPOD)
        }
    }

    open fun deserialize(item: APODPhoto): APOD {

        //Use the date as a unique value (convertFromDate Calendar method) - it will be unique for each day...
        val id = convertFromDate(item.date)

        val apod = APOD(
            id,
            item.date,
            item.explanation,
            item.url
        )

        apod.fetchedFromDjangoService = item.fetchedFromDjangoService

        return apod

    }

    fun searchNextPage(query: String, earth_date: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextPhotoPageTask(
            query = query,
            photoService = photoService,
            earth_date = earth_date,
            db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun insertMarsRoverPhoto(photo: MarsRoverPhoto) {
        //on a background thread
        appExecutors.diskIO().execute(InsertMarsRoverPhotoRunnable(photo))
    }

    fun getAPODComments(apodId: Long): LiveData<Resource<APODWithComments>> {

        return object :
            NetworkBoundResource<APODWithComments, DjangoCommentApiResponse>(appExecutors) {
            override fun saveCallResult(item: DjangoCommentApiResponse) {
                photoDao.insertComments(deserializeDjangoCommentApiResponse(item))
            }

            override fun shouldFetch(data: APODWithComments?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<APODWithComments> {
                return photoDao.getAPODWithComments(apodId)
            }

            override fun createCall(): LiveData<ApiResponse<DjangoCommentApiResponse>> {
                return djangoService.getAPODComments(
                    authToken = Constants.adminToken,
                    page = 1,
                    apod_id = apodId
                )
            }
        }.asLiveData()
    }

    fun getMarsRoverComments(marsRoverPhotoId: Int): LiveData<Resource<MarsRoverPhotoWithComments>> {

        return object :
            NetworkBoundResource<MarsRoverPhotoWithComments, DjangoCommentApiResponse>(appExecutors) {

            override fun saveCallResult(item: DjangoCommentApiResponse) {
                photoDao.insertComments(deserializeDjangoCommentApiResponse(item))
            }

            override fun shouldFetch(data: MarsRoverPhotoWithComments?): Boolean {
                return data == null
            }

            override fun loadFromDb(): LiveData<MarsRoverPhotoWithComments> {
                return photoDao.getMarsRoverPhotoWithComments(marsRoverPhotoId)
            }

            override fun createCall(): LiveData<ApiResponse<DjangoCommentApiResponse>> {
                return djangoService.getMarsRoverComments(
                    authToken = Constants.adminToken,
                    page = 1
                )
            }
        }.asLiveData()

    }

    fun deserializeDjangoCommentApiResponse(item: DjangoCommentApiResponse): List<Comment> {
        val comments = arrayListOf<Comment>()

        item.results.forEach {
            comments.add(
                Comment(
                    it.user_creator_id,
                    it.apod_id,
                    it.mars_rover_id,
                    it.comment_body,
                    DateUtil.formatDate(it.date_updated),
                    it.comment_likes,
                    it.authorName,
                    it.author_profile_pic
                )
            )
        }

        return comments
    }

    fun insertAPOD(apod: APOD) {
        appExecutors.diskIO().execute(InsertAPODRunnable(apod))
    }

    fun saveAPODComment(comment: Comment, apod: APOD) {
        //save the associated apodPhoto to the back-end so it exists.
        saveAPODToDjangoService(apod)

        //save the comment to the back-end
        saveCommentToDjangoService(comment)

    }

    fun saveAPODToDjangoService(apod: APOD) {

        if (!checkAPODExistsRemotely(apod)) {
            appExecutors.networkIO().execute {
                val response = djangoService.saveAPODPhoto(
                    authToken = Constants.adminToken,
                    apod_id = apod.id,
                    date = apod.date,
                    explanation = apod.explanation,
                    url = apod.hdURL
                )

                response.enqueue(object : Callback<DjangoAPODPhotoApiResponse.APODPhoto> {

                    override fun onResponse(
                        call: Call<DjangoAPODPhotoApiResponse.APODPhoto>,
                        response: Response<DjangoAPODPhotoApiResponse.APODPhoto>
                    ) {
                    }

                    override fun onFailure(
                        call: Call<DjangoAPODPhotoApiResponse.APODPhoto>,
                        t: Throwable
                    ) {
                    }
                })
            }
        }
    }

    private fun checkAPODExistsRemotely(apod: APOD): Boolean {
        return apod.fetchedFromDjangoService
    }

    fun deleteComment(comment: Comment) {
        deleteCommentFromDjangoService(comment)

        deleteCommentFromCache(comment)
    }

    fun deleteCommentFromCache(comment: Comment) {
        appExecutors.diskIO().execute {
            photoDao.deleteComment(comment.userCreatorId)
        }
    }

    fun deleteCommentFromDjangoService(comment: Comment) {
        appExecutors.networkIO().execute {
            val response = djangoService.deleteComment(
                authToken = Constants.adminToken,
                user_creator_id = comment.userCreatorId
            )

            response.enqueue(object : Callback<DjangoDeleteCommentApiResponse> {
                override fun onResponse(
                    call: Call<DjangoDeleteCommentApiResponse>,
                    response: Response<DjangoDeleteCommentApiResponse>
                ) {
                }

                override fun onFailure(call: Call<DjangoDeleteCommentApiResponse>, t: Throwable) {
                }
            })
        }
    }

    fun saveCommentToDjangoService(comment: Comment) {
        appExecutors.networkIO().execute {
            val response = djangoService.saveComment(
                authToken = Constants.adminToken,
                comment_id = nextInt(),
                comment_number = 33,
                comment_body = comment.comment,
                date_published = 123,
                date_updated = comment.dateTime,
                author_name = comment.author_name,
                comment_likes = nextInt(),
                mars_rover_id = comment.marsRoverPhotoId,
                apod_id = comment.apodId,
                author_profile_pic = comment.profile_picture,
                user_creator_id = comment.userCreatorId
            )

            response.enqueue(object : Callback<DjangoCommentApiResponse.DjangoCommentResponse> {
                override fun onResponse(
                    call: Call<DjangoCommentApiResponse.DjangoCommentResponse>,
                    response: Response<DjangoCommentApiResponse.DjangoCommentResponse>
                ) {
                }

                override fun onFailure(
                    call: Call<DjangoCommentApiResponse.DjangoCommentResponse>,
                    t: Throwable
                ) {
                }
            })
        }
    }

    fun insertMarsRoverPhotoIntoDjangoService(marsRoverPhoto: MarsRoverPhoto) {
        appExecutors.networkIO().execute {
            val response = djangoService.saveMarsRoverPhoto(
                authToken = Constants.adminToken,
                mars_rover_photo_id = marsRoverPhoto.id.toString(), //using a String here necessary(?) - using Form encoded data
                earth_date = marsRoverPhoto.earth_date,
                image_url = marsRoverPhoto.image_href,
                rover_name = marsRoverPhoto.rover.name,
                camera_name = marsRoverPhoto.camera.name
            )

            response.enqueue(object : Callback<MarsRoverPhoto> {

                override fun onResponse(
                    call: Call<MarsRoverPhoto>,
                    response: Response<MarsRoverPhoto>
                ) {
                }

                override fun onFailure(call: Call<MarsRoverPhoto>, t: Throwable) {
                }
            })
        }
    }

    inner class InsertMarsRoverPhotoRunnable(val photo: MarsRoverPhoto) : Runnable {
        override fun run() {
            photoDao.insertMarsRoverPhoto(photo)
        }
    }

    inner class InsertAPODRunnable(val apod: APOD) : Runnable {
        override fun run() {
            photoDao.insertCacheApodPhoto(apod)
        }
    }

}