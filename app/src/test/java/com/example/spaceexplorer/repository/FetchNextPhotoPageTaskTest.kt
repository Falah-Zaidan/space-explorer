package com.example.spaceexplorer.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.db.NASADatabase
import com.example.spaceexplorer.cache.model.MarsRoverApiResult
import com.example.spaceexplorer.remote.PhotoService
import com.example.spaceexplorer.repository.util.FetchNextPhotoPageTask
import com.example.spaceexplorer.util.Resource
import com.example.spaceexplorer.util.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class FetchNextPhotoPageTaskTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: PhotoService

    private lateinit var db: NASADatabase

    private lateinit var photoDao: PhotoDao

    private lateinit var task: FetchNextPhotoPageTask

    private val observer: Observer<Resource<Boolean>> = mock()

    @Before
    fun init() {
        service = mock(PhotoService::class.java)
        db = mock(NASADatabase::class.java)
        `when`(db.runInTransaction(any())).thenCallRealMethod()
        photoDao = mock(PhotoDao::class.java)
        `when`(db.photoDao()).thenReturn(photoDao)
        task = FetchNextPhotoPageTask("2015-07-06", "query", service, db)
        task.liveData.observeForever(observer)
    }

    @Test
    fun withoutResult() {
        `when`(photoDao.getPhotos()).thenReturn(null)
        task.run()
        verify(observer).onChanged(null)
        verifyNoMoreInteractions(observer)
        verifyNoMoreInteractions(service)
    }

//    @Test
//    fun noNextPage() {
//        createDbResult(null)
//        task.run()
//        verify(observer).onChanged(Resource.success(false))
//        verifyNoMoreInteractions(observer)
//        verifyNoMoreInteractions(service)
//    }

//    @Test
//    fun nextPageWithNull() {
//        createDbResult(1)
//        val repos = TestUtil.createRepos(10, "a", "b", "c")
//        val result = RepoSearchResponse(10, repos)
//        val call = createCall(result, null)
//        `when`(service.searchRepos("foo", 1)).thenReturn(call)
//        task.run()
//        verify(repoDao).insertRepos(repos)
//        verify(observer).onChanged(Resource.success(false))
//    }

//    @Test
//    fun nextPageWithMore() {
//
//        //create a db result (i.e. just a class that holds the 'next value' and photos
//        createDbResult(1)
//        val photos = TestUtil.createMarsRoverPhotos(10)
//
//        val result = MarsRoverApiResult()
//        result.next = 2
//        val call = createCall(result, 2)
////        `when`(service.searchRepos("foo", 1)).thenReturn(call)
////        task.run()
////        verify(repoDao).insertRepos(repos)
////        verify(observer).onChanged(Resource.success(true))
//    }

//    @Test
//    fun nextPageApiError() {
//        createDbResult(1)
//        val call = mock<Call<RepoSearchResponse>>()
//        `when`(call.execute()).thenReturn(
//            Response.error(
//                400, ResponseBody.create(
//                    MediaType.parse("txt"), "bar"
//                )
//            )
//        )
//        `when`(service.searchRepos("foo", 1)).thenReturn(call)
//        task.run()
//        verify(observer)!!.onChanged(Resource.error("bar", true))
//    }

//    @Test
//    fun nextPageIOError() {
//        createDbResult(1)
//        val call = mock<Call<RepoSearchResponse>>()
//        `when`(call.execute()).thenThrow(IOException("bar"))
//        `when`(service.searchRepos("foo", 1)).thenReturn(call)
//        task.run()
//        verify(observer)!!.onChanged(Resource.error("bar", true))
//    }

    private fun createDbResult(nextPage: Int?) {
        val result = MarsRoverApiResult(
            nextPage
        )

        //This is your own implementation - you are storing all 'MarsRoverApiResult' values in a list...
        val resultList = mutableListOf<MarsRoverApiResult>()
        resultList.add(result)

        `when`(photoDao.getMarsRoverApiResult()).thenReturn(resultList)
    }

//    private fun createCall(body: RepoSearchResponse, nextPage: Int?): Call<RepoSearchResponse> {
//        val headers = if (nextPage == null)
//            null
//        else
//            Headers
//                .of(
//                    "link",
//                    "<https://api.github.com/search/repositories?q=foo&page=" + nextPage
//                            + ">; rel=\"next\""
//                )
//        val success = if (headers == null)
//            Response.success(body)
//        else
//            Response.success(body, headers)
//        val call = mock<Call<RepoSearchResponse>>()
//        `when`(call.execute()).thenReturn(success)
//
//        return call
//    }
}