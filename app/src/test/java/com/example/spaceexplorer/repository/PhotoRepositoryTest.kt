package com.example.spaceexplorer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.db.NASADatabase
import com.example.spaceexplorer.remote.DjangoService
import com.example.spaceexplorer.remote.PhotoService
import com.example.spaceexplorer.util.InstantAppExecutors
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class PhotoRepositoryTest {
    private lateinit var repository: PhotoRepository
    private val dao = mock(PhotoDao::class.java)
    private val service = mock(PhotoService::class.java)
    private val djangoService = mock(DjangoService::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val db = mock(NASADatabase::class.java)
        `when`(db.photoDao()).thenReturn(dao)
        `when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = PhotoRepository(InstantAppExecutors(), db, dao, service, djangoService)
    }

    @Test
    fun loadApodPhoto() {
//        //ApodPhoto - must test others
//        val dbData = MutableLiveData<APOD>()
//        val apodDate = "2015-05-03"
//        val apiKey = "5vv4KVOuKmLQ852nlkS2WTpjofWpcjnAj4yj1NdO"
//        `when`(dao.getAPOD(apodDate)).thenReturn(dbData)
//
//        val apodPhoto = TestUtil.createApodPhoto(apodDate)
//        val call = successCall(apodPhoto)
//        `when`(service.getAstronomyPictureOfTheDay(apodDate, apiKey)).thenReturn(call)
//
//        //Do a basic call on the repository method to ensure stubs are invoked
//        //Db invoked, no Network (NBR is meant to fetch from DB first before Network)
//        val data = repository.loadAPOD(apodDate)
//        verify(dao).getAPOD(apodDate)
//        verifyNoMoreInteractions(service)
//
//        //Create an Observer and observe the repository (method)
//        //Ensure no Network interactions, stub new data when dao method is called
//        val observer = mock<Observer<Resource<APOD>>>()
//        data.observeForever(observer)
//        verifyNoMoreInteractions(service)
//        verify(observer).onChanged(Resource.loading(null))
//        val updatedDbData = MutableLiveData<APOD>()
//        `when`(dao.getAPOD(apodDate)).thenReturn(updatedDbData)
//
//        val newAPOD = TestUtil.createApod("2015-02-03")
//        `when`(repository.deserialize(apodPhoto)).thenReturn(newAPOD)
//
//        //post a null value on the dbData
//        //check that getAPOD called on the service -
//        //since NBR will fetch if the Dao value is null (specified in the 'shouldFetch() method'
//        //check that NBR puts it into db i.e. 'saveCallResult'
//        //check that NBR fetches it from Dao i.e. 'FetchFromDB'
//        //check that the observer to the particular instance of the repository '.loadAPOD()' is updated
//        dbData.postValue(null)
//        verify(service).getAstronomyPictureOfTheDay(
//            apodDate,
//            apiKey
//        )
//
//        //issue here is that id is different - probably because you aren't meant to deserialize in the test class...
////        verify(dao).insertAPOD(newAPOD)
//
//        updatedDbData.postValue(newAPOD)
//        verify(observer).onChanged(Resource.success(newAPOD))
    }

    @Test
    fun loadMarsRoverPhotos() {
//        val dbData = MutableLiveData<List<MarsRoverPhoto>>()
//        `when`(dao.getPhotosByRoverNameAndDate(anyString(), anyString())).thenReturn(dbData)
//
//        val marsRoverPhotoApiResponse = TestUtil.createMarsRoverApiResponse()
//        val call = successCall(marsRoverPhotoApiResponse)
//
//        `when`(
//            service.searchPhotosByDate(
//                anyString(), anyString(), anyString(), anyInt()
//            )
//        ).thenReturn(call)
//
//        val data = repository.loadMarsRoverPhotos("aa", "bb")
//        verify(dao).getPhotosByRoverNameAndDate("aa", "bb")
//        verifyNoMoreInteractions(service)
//
//        //Create an Observer and observe the repository (method)
//        //Ensure no Network interactions, stub new data when dao method is called
//        val observer = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
//        data.observeForever(observer)
//        verifyNoMoreInteractions(service)
//        verify(observer).onChanged(Resource.loading(null))
//        val updatedDbData = MutableLiveData<List<MarsRoverPhoto>>()
//        `when`(dao.getPhotosByRoverNameAndDate(anyString(), anyString())).thenReturn(updatedDbData)
//
//        dbData.postValue(null)
//        verify(service).searchPhotosByDate(anyString(), anyString(), anyString(), anyInt())
//        verify(dao).insertPhotos(marsRoverPhotoApiResponse.marsRoverPhotos)
//
//        updatedDbData.postValue(marsRoverPhotoApiResponse.marsRoverPhotos)
//        verify(observer).onChanged(Resource.success(marsRoverPhotoApiResponse.marsRoverPhotos))
    }

}