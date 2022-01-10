package com.example.spaceexplorer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.spaceexplorer.cache.dao.FavouriteDao
import com.example.spaceexplorer.cache.db.NASADatabase
import com.example.spaceexplorer.remote.DjangoService
import com.example.spaceexplorer.util.InstantAppExecutors
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

//@RunWith(JUnit4::class)
//class FavouriteRepositoryTest {
//    private lateinit var repository: FavouriteRepository
//    private val dao = mock(FavouriteDao::class.java)
//    private val service = mock(DjangoService::class.java)
//
//    @Rule
//    @JvmField
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    @Before
//    fun init() {
//        val db = mock(NASADatabase::class.java)
//        `when`(db.favouriteDao()).thenReturn(dao)
//        `when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
//        repository = FavouriteRepository(InstantAppExecutors(), db, dao, service)
//    }
//
//
//    //Need to test for others?
////    @Test
////    fun loadFavourites() {
////        val dbData = MutableLiveData<List<Favourite>>()
////        `when`(dao.getFavourites()).thenReturn(dbData)
////
////        val favourite = TestUtil.createApod()
////        val call = successCall(apodPhoto)
////        `when`(service.getAstronomyPictureOfTheDay(apodDate, apiKey)).thenReturn(call)
////
////        //Do a basic call on the repository method to ensure stubs are invoked
////        //Db invoked, no Network (NBR is meant to fetch from DB first before Network)
////        val data = repository.loadAPOD(apodDate)
////        verify(dao).getAPODPhoto(apodDate)
////        verifyNoMoreInteractions(service)
////
////        //Create an Observer and observe the repository (method)
////        //Ensure no Network interactions, stub new data when dao method is called
////        val observer = mock<Observer<Resource<APODPhoto>>>()
////        data.observeForever(observer)
////        verifyNoMoreInteractions(service)
////        verify(observer).onChanged(Resource.loading(null))
////        val updatedDbData = MutableLiveData<APODPhoto>()
////        `when`(dao.getAPODPhoto(apodDate)).thenReturn(updatedDbData)
////
////        //post a null value on the dbData
////        //check that getAPOD called on the service -
////        //since NBR will fetch if the Dao value is null (specified in the 'shouldFetch() method'
////        //check that NBR puts it into db i.e. 'saveCallResult'
////        //check that NBR fetches it from Dao i.e. 'FetchFromDB'
////        //check that the observer to the particular instance of the repository '.loadAPOD()' is updated
////        dbData.postValue(null)
////        verify(service).getAstronomyPictureOfTheDay(
////            apodDate,
////            apiKey
////        )
////        verify(dao).insertAPODPhoto(apodPhoto)
////
////        updatedDbData.postValue(apodPhoto)
////        verify(observer).onChanged(Resource.success(apodPhoto))
////    }
//}