package com.example.spaceexplorer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.spaceexplorer.repository.PhotoRepository
import com.example.spaceexplorer.util.Resource
import com.example.spaceexplorer.viewmodels.ListViewModel
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class NextPageHandlerTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(PhotoRepository::class.java)

    private lateinit var pageHandler: ListViewModel.NextPageHandler

    @Before
    fun init() {
        pageHandler = ListViewModel.NextPageHandler(repository)
    }

    private val status: ListViewModel.LoadMoreState?
        get() = pageHandler.loadMoreState.value

    @Test
    fun constructor() {
        val initial = status
        assertThat<ListViewModel.LoadMoreState>(initial, notNullValue())
        assertThat(initial?.isRunning, `is`(false))
        assertThat(initial?.errorMessage, nullValue())
    }

    @Test
    fun reloadSameValue() {
        enqueueResponse("foo")
        pageHandler.queryNextPage("foo", "2015-03-02")
        verify(repository).searchNextPage("foo", "2015-03-02")

        reset(repository)
        pageHandler.queryNextPage("foo", "2015-03-02")
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun success() {
        val liveData = enqueueResponse("foo")

        pageHandler.queryNextPage("foo", "2015-03-02")
        verify(repository).searchNextPage("foo", "2015-03-02")
        assertThat(liveData.hasActiveObservers(), `is`(true))
        pageHandler.onChanged(Resource.loading(null))
        assertThat(liveData.hasActiveObservers(), `is`(true))
        assertThat(status?.isRunning, `is`(true))

        pageHandler.onChanged(Resource.success(true))
        assertThat(liveData.hasActiveObservers(), `is`(false))
        assertThat(pageHandler.hasMore, `is`(true))
        assertThat(status?.isRunning, `is`(false))
        assertThat(liveData.hasActiveObservers(), `is`(false))

        // requery
        reset(repository)
        val nextPage = enqueueResponse("foo")
        pageHandler.queryNextPage("foo", "2015-03-02")
        verify(repository).searchNextPage("foo", "2015-03-02")
        assertThat(nextPage.hasActiveObservers(), `is`(true))

        pageHandler.onChanged(Resource.success(false))
        assertThat(liveData.hasActiveObservers(), `is`(false))
        assertThat(pageHandler.hasMore, `is`(false))
        assertThat(status?.isRunning, `is`(false))
        assertThat(nextPage.hasActiveObservers(), `is`(false))

        // retry, no query
        reset(repository)
        pageHandler.queryNextPage("foo", "2015-03-02")
        verifyNoMoreInteractions(repository)
        pageHandler.queryNextPage("foo", "2015-03-02")
        verifyNoMoreInteractions(repository)

        // query another
        val bar = enqueueResponse("bar")
        pageHandler.queryNextPage("bar", "2015-03-02")
        verify(repository).searchNextPage("bar", "2015-03-02")
        assertThat(bar.hasActiveObservers(), `is`(true))
    }

    @Test
    fun failure() {
        val liveData = enqueueResponse("foo")
        pageHandler.queryNextPage("foo", "2015-03-02")
        assertThat(liveData.hasActiveObservers(), `is`(true))
        pageHandler.onChanged(Resource.error("idk", false))
        assertThat(liveData.hasActiveObservers(), `is`(false))
        assertThat(status?.errorMessage, `is`("idk"))
        assertThat(status?.errorMessageIfNotHandled, `is`("idk"))
        assertThat(status?.errorMessageIfNotHandled, nullValue())
        assertThat(status?.isRunning, `is`(false))
        assertThat(pageHandler.hasMore, `is`(true))

        reset(repository)
        val liveData2 = enqueueResponse("foo")
        pageHandler.queryNextPage("foo", "2015-03-02")
        assertThat(liveData2.hasActiveObservers(), `is`(true))
        assertThat(status?.isRunning, `is`(true))
        pageHandler.onChanged(Resource.success(false))
        assertThat(status?.isRunning, `is`(false))
        assertThat(status?.errorMessage, `is`(nullValue()))
        assertThat(pageHandler.hasMore, `is`(false))
    }

    @Test
    fun nullOnChanged() {
        val liveData = enqueueResponse("foo")
        pageHandler.queryNextPage("foo", "2015-03-02")
        assertThat(liveData.hasActiveObservers(), `is`(true))
        pageHandler.onChanged(null)
        assertThat(liveData.hasActiveObservers(), `is`(false))
    }

    private fun enqueueResponse(query: String): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()
        `when`(repository.searchNextPage(query, "2015-03-02")).thenReturn(liveData)
        return liveData
    }
}