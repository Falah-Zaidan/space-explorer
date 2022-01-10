package com.example.spaceexplorer.ui

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.cache.model.CurrentAPOD
import com.example.spaceexplorer.data.DataFactory
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString

@RunWith(AndroidJUnit4::class)
class ApodFragmentTest {

    private val navController = mock<NavController>()
    private lateinit var listViewModel: ListViewModel

    private var apodPhotoLiveData = MutableLiveData<Resource<APOD>>()
    private var currentAPODLiveData = MutableLiveData<Resource<CurrentAPOD>>()

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    @Before
    fun init() {
        mockBindingAdapter = mock()
        listViewModel = mock()

        whenever(listViewModel.apod).thenReturn(apodPhotoLiveData)
        whenever(listViewModel.currentAPODLiveData).thenReturn(currentAPODLiveData)

        val mViewModelFactoryProvider = ViewModelUtil.createFor(listViewModel)

        val scenario =
            launchFragmentInContainer(ApodFragmentArgs("").toBundle(), R.style.AppTheme) {
                ApodFragment().apply {
                    viewModelProviderFactory = mViewModelFactoryProvider
                    databindingComponent = object : DataBindingComponent {
                        override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                            return mockBindingAdapter
                        }
                    }
                }
            }

        dataBindingIdlingResourceRule.monitorFragment(scenario)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun loading() {
        apodPhotoLiveData.postValue(Resource.loading(null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun error() {
        apodPhotoLiveData.postValue(Resource.error("wtf", null))
        Espresso.onView(withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(withId(R.id.error_msg))
            .check(ViewAssertions.matches(ViewMatchers.withText("wtf")))
        Espresso.onView(withId(R.id.retry))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun loadingWithSimpleItem() {
        val apodList = DataFactory.makeAPODList(10) //make a photo list
        val apodItem = apodList[0] //get a single item from the list (first)

        apodPhotoLiveData.postValue(Resource.loading(apodItem))

        Espresso.onView(ViewMatchers.withId(R.id.image_description))
            .check(ViewAssertions.matches(ViewMatchers.withText(apodItem.explanation)))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun loadedSimpleItem() {
        val apodList = DataFactory.makeAPODList(10) //make a photo list
        val apodItem = apodList[0] //get a single item from the list (first)

        apodPhotoLiveData.postValue(Resource.success(apodItem))

        Espresso.onView(ViewMatchers.withId(R.id.image_description))
            .check(ViewAssertions.matches(ViewMatchers.withText(apodItem.explanation)))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun nextButtonClicked() {
        onView(withId(R.id.nextAPOD)).perform(click())
        verify(listViewModel).nextAPOD()

        //post LiveData for another random apod model to the LiveData... and see whether it is now displayed in the UI...
        val apod = DataFactory.makeAPOD()
        apodPhotoLiveData.postValue(Resource.success(apod))

        onView(withId(R.id.image_description)).check(matches(withText(apod.explanation)))
        onView(withId(R.id.favourite_filled)).check(matches(not(isDisplayed())))
        onView(withId(R.id.favourite_border)).check(matches(isDisplayed()))
    }

    @Test
    fun previousButtonClicked() {
        onView(withId(R.id.previousAPOD)).perform(click())
        verify(listViewModel).previousAPOD()

        val apod = DataFactory.makeAPOD()
        apodPhotoLiveData.postValue(Resource.success(apod))

        onView(withId(R.id.image_description)).check(matches(withText(apod.explanation)))
        onView(withId(R.id.favourite_filled)).check(matches(not(isDisplayed())))
        onView(withId(R.id.favourite_border)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCommentButton() {

        val apodList = DataFactory.makeAPODList(1) //make a photo list
        val apodItem = apodList[0] //get a single item from the list (first)

        apodPhotoLiveData.postValue(Resource.success(apodItem))

        onView(withId(R.id.comment_btn)).perform(click())

        verify(navController).navigate(
            ApodFragmentDirections.actionApodFragmentToCommentFragment(
                apodItem.id, -1, anyString()
            )
        )
    }

    @Test
    fun testCalendar() {

    }

    @Test
    fun likeButtonClicked() {

    }

    @Test
    fun nullSimpleItem() {

    }

}