package com.example.spaceexplorer.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.data.DataFactory
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class SpiritFragmentTest {

    private val navController = mock<NavController>()
    private lateinit var listViewModel: ListViewModel

    private var spiritListLiveData = MutableLiveData<Resource<List<MarsRoverPhoto>>>()
    private val loadMoreStatus = MutableLiveData<ListViewModel.LoadMoreState>()

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    @Before
    fun init() {
        mockBindingAdapter = mock()
        listViewModel = mock()

        whenever(listViewModel.spiritPhotos).thenReturn(spiritListLiveData)
        whenever(listViewModel.getLoadMoreStatus()).thenReturn(loadMoreStatus)

        val mViewModelFactoryProvider = ViewModelUtil.createFor(listViewModel)

        val scenario = launchFragmentInContainer(null, R.style.AppTheme) {
            SpiritRoverFragment().apply {
                viewModelProviderFactory = mViewModelFactoryProvider
                appExecutors = countingAppExecutors.appExecutors
            }
        }

        dataBindingIdlingResourceRule.monitorFragment(scenario)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    //TODO: doesn't seem to pass when running the whole class, but works on it's own...
    @Test
    fun clickOtherRover() {
        onView(withId(R.id.button_curiosity)).perform(click())
        verify(navController).navigate(SpiritRoverFragmentDirections.actionSpiritRoverFragmentToCuriosityRoverFragment())
    }

    @Test
    fun loading() {
        spiritListLiveData.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun error() {
//        doNothing().`when`(listViewModel).retry()
        spiritListLiveData.postValue(Resource.error("error", null))
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.error_msg))
            .check(matches(withText("error")))
        onView(withId(R.id.retry))
            .check(matches(isDisplayed()))
        onView(withId(R.id.retry)).perform(click())
        verify(listViewModel).retry()
    }

    @Test
    fun loadingWithItems() {
        val photoList = DataFactory.makePhotoList(10) //make a photo list
        val photoItem = photoList[0] //get a single item from the list (first)

        spiritListLiveData.postValue(Resource.loading(photoList))

        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText(photoItem.camera.name))))
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun successfullyLoadedItems() {
        val photoList = DataFactory.makePhotoList(10) //make a photo list
        val photoItem = photoList[0] //get a single item from the list (first)

        spiritListLiveData.postValue(Resource.success(photoList))
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText(photoItem.camera.name))))
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun initialLoadMore() {
//        doNothing().`when`(listViewModel.loadNextPage())
        val itemList = DataFactory.makePhotoList(50)
        spiritListLiveData.postValue(Resource.success(itemList))
        val action = scrollToPosition<RecyclerView.ViewHolder>(49)
        onView(withId(R.id.recyclerview_feed)).perform(action)
        onView(listMatcher().atPosition(49))
            .check(matches(isDisplayed()))
        //verify that the nextPage for the right rover is queried...
        Mockito.verify(listViewModel).loadNextPage("Spirit")
    }

    @Test
    fun loadMoreProgress() {
        loadMoreStatus.postValue(ListViewModel.LoadMoreState(true, null))
        onView(withId(R.id.load_more_bar))
            .check(matches(isDisplayed()))
        loadMoreStatus.postValue(ListViewModel.LoadMoreState(false, null))
        onView(withId(R.id.load_more_bar))
            .check(matches(not(isDisplayed())))
    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.recyclerview_feed)

}