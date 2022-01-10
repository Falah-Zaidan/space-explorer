package com.example.spaceexplorer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.spaceexplorer.cache.dao.PhotoDao
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.repository.FavouriteRepository
import com.example.spaceexplorer.repository.PhotoRepository
import com.example.spaceexplorer.util.Resource
import com.example.spaceexplorer.util.mock
import com.example.spaceexplorer.viewmodels.ListViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class ListViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()
    private val photoRepository = mock(PhotoRepository::class.java)
    private val favouriteRepository = mock(FavouriteRepository::class.java)
    private lateinit var listViewModel: ListViewModel
    private val photoDao = mock(PhotoDao::class.java)

    @Before
    fun init() {
        listViewModel = ListViewModel(favouriteRepository, photoRepository, photoDao)
    }

    //Testing all 3 streams individually
    @Test
    fun setValueAndObservePhotoResult() {

        //spirit
        val spiritResult = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
        listViewModel.spiritPhotos.observeForever(spiritResult)
        verify(photoRepository).loadMarsRoverPhotos("Spirit", "2004-02-13")
        verify(photoRepository, never()).searchNextPage("Spirit", "2004-02-13")
        verifyNoMoreInteractions(photoRepository)

        //curiosity
        val curiosityResult = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
        listViewModel.curiosityPhotos.observeForever(curiosityResult)
        verify(photoRepository).loadMarsRoverPhotos("Curiosity", "2015-12-02")
        verify(photoRepository, never()).searchNextPage("Curiosity", "2015-12-02")
        verifyNoMoreInteractions(photoRepository)

        //opportunity
        val opportunityResult = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
        listViewModel.opportunityPhotos.observeForever(opportunityResult)
        verify(photoRepository).loadMarsRoverPhotos("Opportunity", "2004-01-26")
        verify(photoRepository, never()).searchNextPage("Opportunity", "2004-01-26")
        verifyNoMoreInteractions(photoRepository)

    }

    @Test
    fun setAPODDate() {
        val apodResult = mock<Observer<Resource<APOD>>>()
        listViewModel.apod.observeForever(apodResult)
        listViewModel.setApodDate("2007-05-04")
        verify(photoRepository).loadAPOD("2007-05-04")

        listViewModel.setApodDate("2010-04-05")
        verify(photoRepository).loadAPOD("2010-04-05")

    }

    @Test
    fun setRoverDate() {
        val spiritResult = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
        listViewModel.spiritPhotos.observeForever(spiritResult)
        verify(photoRepository).loadMarsRoverPhotos("Spirit", "2004-02-13")
        verify(photoRepository, never()).searchNextPage("Spirit", "2004-02-13")
        verifyNoMoreInteractions(photoRepository)

        listViewModel.setSpiritDate("2004-03-13")
        verify(photoRepository).loadMarsRoverPhotos("Spirit", "2004-03-13")
        verify(photoRepository, never()).searchNextPage("Spirit", "2004-03-13")
        verifyNoMoreInteractions(photoRepository)

    }

    @Test
    fun refresh() {
        val spiritResult = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
        listViewModel.spiritPhotos.observeForever(spiritResult)
        verify(photoRepository).loadMarsRoverPhotos("Spirit", "2004-02-13")
        verifyNoMoreInteractions(photoRepository)

        reset(spiritResult)
        listViewModel.refresh()

        verify(photoRepository).loadMarsRoverPhotos("Spirit", "2004-02-13")
        verifyNoMoreInteractions(photoRepository)

    }

    @Test
    fun dontFetchWithoutObservers() {
        listViewModel.setSpiritDate("2004-03-14")
        verify(photoRepository, never()).loadMarsRoverPhotos("Spirit", "2004-03-14")
    }

    @Test
    fun changedWhileObserved() {
        listViewModel.apod.observeForever(mock())

        listViewModel.setApodDate("2015-06-03")
        listViewModel.setApodDate("2015-06-04")

        verify(photoRepository).loadAPOD("2015-06-03")
        verify(photoRepository).loadAPOD("2015-06-04")
    }

    @Test
    fun loadNextPage() {
        val spiritResult = mock<Observer<Resource<List<MarsRoverPhoto>>>>()
        listViewModel.spiritPhotos.observeForever(spiritResult)
        verify(photoRepository).loadMarsRoverPhotos("Spirit", "2004-02-13")
        verify(photoRepository, never()).searchNextPage("Spirit", "2004-02-13")
        verifyNoMoreInteractions(photoRepository)

        listViewModel.loadNextPage("Spirit")
        verify(
            photoRepository
        ).searchNextPage(
            "Spirit",
            "2004-02-13"
        )

    }

}