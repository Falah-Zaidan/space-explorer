package com.example.spaceexplorer.viewmodels

import androidx.core.util.Pair
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.cache.model.APODWithComments
import com.example.spaceexplorer.cache.model.Comment
import com.example.spaceexplorer.cache.model.CurrentAPOD
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.repository.FavouriteRepository
import com.example.spaceexplorer.repository.PhotoRepository
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.DateUtil.Companion.convertFromDate
import com.example.spaceexplorer.util.DateUtil.Companion.convertToDate
import com.example.spaceexplorer.util.Resource
import com.example.spaceexplorer.util.Status
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random.Default.nextInt

@OpenForTesting
class ListViewModel @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val photoRepository: PhotoRepository,
    private val photoDao: PhotoDao
) : ViewModel() {

    private val nextPageHandler = NextPageHandler(photoRepository)

    val mApodDate = MutableLiveData<String>(convertToDate(Calendar.getInstance().time.time))

    val mCuriosityNameDatePair =
        MutableLiveData<Pair<String, String>>(Pair.create(Constants.curiosty_text, Constants.curiosity_initial_date))
    val mSpiritNameDatePair =
        MutableLiveData<Pair<String, String>>(Pair.create(Constants.spirit_text, Constants.spirit_initial_date))
    val mOpportunityNameDatePair =
        MutableLiveData<Pair<String, String>>(Pair.create(Constants.opportunity_text, Constants.opporunity_initial_date))


    fun getAPODLiveData(date: String): LiveData<Resource<APOD>> {
        return photoRepository.loadAPOD(date)
    }

    val apod: LiveData<Resource<APOD>> =
        mApodDate!!.switchMap {
            photoRepository.loadAPOD(it)
        }

    private fun emptyLiveData(): LiveData<Resource<APOD>> {
        val liveData = MutableLiveData(
            Resource.error(
                "error",
                APOD(-1, nextInt().toString(), nextInt().toString(), nextInt().toString())
            )
        )
        return liveData
    }

    val curiosityPhotos: LiveData<Resource<List<MarsRoverPhoto>>> =
        mCuriosityNameDatePair.switchMap {
            photoRepository.loadMarsRoverPhotos(it.first!!, it.second!!)
        }

    val spiritPhotos: LiveData<Resource<List<MarsRoverPhoto>>> =
        mSpiritNameDatePair.switchMap {
            photoRepository.loadMarsRoverPhotos(it.first!!, it.second!!)
        }

    val opportunityPhotos: LiveData<Resource<List<MarsRoverPhoto>>> =
        mOpportunityNameDatePair.switchMap {
            photoRepository.loadMarsRoverPhotos(it.first!!, it.second!!)
        }

    var currentAPODLiveData = MutableLiveData<Resource<CurrentAPOD>>()

    fun getAPODPhoto(date: String): LiveData<Resource<APOD>> {
        return photoRepository.loadAPOD(date)
    }

    val spiritDateBounds = Pair.create(
        convertFromDate(Constants.spirit_start_bound),
        convertFromDate(Constants.spirit_end_bound)
    )

    val opportunityDateBounds = Pair.create(
        convertFromDate(Constants.opportunity_start_bound),
        convertFromDate(Constants.opportunity_end_bound)
    )

    val curiosityDateBounds = Pair.create(
        convertFromDate(Constants.curiosity_start_bound),
        convertFromDate(Constants.curiosity_end_bound)
    )

    fun setCuriosityDate(date: String) {
        mCuriosityNameDatePair.value = Pair.create(Constants.curiosty_text, date)
    }

    fun setSpiritDate(date: String) {
        mSpiritNameDatePair.value = Pair.create(Constants.spirit_text, date)
    }

    fun setOpportunityDate(date: String) {
        mOpportunityNameDatePair.value = Pair.create(Constants.opportunity_text, date)
    }

    fun setApodDate(date: String?) {
        if (!date.isNullOrEmpty()) {
            mApodDate.value = date
        }
    }

    fun refresh() {
        val date = mApodDate!!.value
        mApodDate!!.value = date
    }

    fun retry() {

    }

    //the order of 'setCurrentAPOD', 'setAPODDate' very important (in the below 2 methods)
    fun nextAPOD() {
        if (checkValidInput(true)) {
            setCurrentAPOD(CurrentAPOD(nextDayFormatted()))
            setApodDate(nextDayFormatted())
        }
    }

    fun previousAPOD() {
        if (checkValidInput(false)) {
            setCurrentAPOD(CurrentAPOD(previousDayFormatted()))
            setApodDate(previousDayFormatted())
        }
    }

    fun previousDayFormatted(): String {
        val currentDate = mApodDate!!.value
        val unformattedDate = convertFromDate(currentDate!!)

        //decrement unformatted Date by one day
        val previousDayUnformatted = unformattedDate - (360 * 24 * 10000)

        return convertToDate(previousDayUnformatted)
    }

    fun nextDayFormatted(): String {
        //get the current date from MutableLiveData value stored in VM
        //un-format, then add a day in this form
        //re-format
        val currentDate = mApodDate!!.value
        val unformattedDate = convertFromDate(currentDate!!)

        //increment unformatted Date by one day
        val nextDayUnformatted = unformattedDate + (360 * 24 * 10000)

        return convertToDate(nextDayUnformatted)
    }

    fun getPhoto(photoId: Long): LiveData<Resource<MarsRoverPhoto>> {
        return photoRepository.loadPhotoById(photoId)
    }

    fun getLoadMoreStatus(): LiveData<LoadMoreState> {
        return nextPageHandler.loadMoreState
    }

    fun loadNextPage(currentSelectedRover: String) {

        var query = ""
        var date = ""

        when (currentSelectedRover) {
            Constants.curiosty_text -> {
                query = mCuriosityNameDatePair.value!!.first!!
                date = mCuriosityNameDatePair.value!!.second!!
            }
            Constants.spirit_text -> {
                query = mSpiritNameDatePair.value!!.first!!
                date = mSpiritNameDatePair.value!!.second!!
            }
            Constants.opportunity_text -> {
                query = mOpportunityNameDatePair.value!!.first!!
                date = mOpportunityNameDatePair.value!!.second!!
            }
        }

        nextPageHandler.queryNextPage(
            query,
            date
        )
    }

    fun checkValidInput(direction: Boolean): Boolean {
        val currentApodDateUnformatted = convertFromDate(mApodDate!!.value!!)
        val currentDate = convertToDate(Calendar.getInstance().time.time)

        val apodDateBounds = Pair.create(
            convertFromDate(Constants.apod_start_bound),
            convertFromDate(currentDate)
        )

        if (direction) { //positive direction i.e. forwards
            return !(currentApodDateUnformatted <= apodDateBounds.first!! || currentApodDateUnformatted >= apodDateBounds.second!!)
        } else {
            return !(currentApodDateUnformatted <= apodDateBounds.first!! || currentApodDateUnformatted > apodDateBounds.second!!)
        }

    }

    fun getAPODWithComments(apodId: Long): LiveData<Resource<APODWithComments>> {
        return photoRepository.getAPODComments(apodId)
    }

    fun getCurrentAPOD() {

        viewModelScope.launch {
            val currentAPOD = photoDao.getCurrentAPOD()
            currentAPODLiveData.value = Resource.success(currentAPOD)

        }
    }

    fun setCurrentAPOD(currentAPOD: CurrentAPOD) {
        if (mApodDate.value != null) {
            if (!mApodDate.value.equals(convertToDate(Calendar.getInstance().time.time))) {
                //we should insert the currentAPOD of the next day... (
                photoRepository.insertCurrentAPOD(currentAPOD)
            }
        }
    }

    fun saveMarsRoverPhoto(photo: MarsRoverPhoto) {
        photoRepository.insertMarsRoverPhotoIntoDjangoService(photo)
    }

    fun setFavouriteMarsRoverPhoto(photo: MarsRoverPhoto) {
        favouriteRepository.saveMarsRoverPhoto(photo)
    }

    fun removeFavouriteMarsRoverPhoto(photo: MarsRoverPhoto) {
        favouriteRepository.deleteFavourite(photo.id)
    }

    fun setFavouriteAPODPhoto(photo: APOD) {
        favouriteRepository.saveAPODPhoto(photo)
    }

    fun removeFavouriteAPODPhoto(photo: APOD) {
        favouriteRepository.deleteFavourite(convertFromDate(photo.date))
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(val stubPhotoRepository: PhotoRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(query: String, earth_date: String) {
            if (this.query == query) {
                return
            }
            unregister()
            this.query = query
            nextPageLiveData =
                stubPhotoRepository.searchNextPage(
                    query,
                    earth_date
                ) //this is usually a method in the repository -- stub at the moment
            loadMoreState.value = LoadMoreState(
                isRunning = true,
                errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = result.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                isRunning = false,
                errorMessage = null
            )
        }

    }

    fun insertMarsRoverPhoto(photo: MarsRoverPhoto) {
        photoRepository.insertMarsRoverPhoto(photo)
    }

    fun insertAPOD(apod: APOD) {
        photoRepository.insertAPOD(apod)
    }

    fun insertAPODComment(apod: APOD, comment: Comment) {
        photoRepository.saveAPODComment(comment, apod)
    }

    fun deleteComment(comment: Comment) {
        photoRepository.deleteComment(comment)
    }

    fun saveAPODToDjangoService(apod: APOD) {
        photoRepository.saveAPODToDjangoService(apod)
    }

    fun saveMarsRoverPhotoToDjangoService(marsRoverPhoto: MarsRoverPhoto) {
        photoRepository.insertMarsRoverPhotoIntoDjangoService(marsRoverPhoto)
    }

}



