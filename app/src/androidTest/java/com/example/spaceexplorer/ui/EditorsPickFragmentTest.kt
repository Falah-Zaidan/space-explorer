package com.example.spaceexplorer.ui

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.viewmodels.EditorsPickViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditorsPickFragmentTest {

    private val navController = mock<NavController>()
    private lateinit var editorsPickViewModel: EditorsPickViewModel

    private var editorsPickLiveData = MutableLiveData<Resource<List<EditorsPickPhoto>>>()

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    @Before
    fun init() {
//        mockBindingAdapter = mock()
//        editorsPickViewModel = mock()
//
//        whenever(editorsPickViewModel._editorsPickLiveData).thenReturn(editorsPickLiveData)
//        val mViewModelFactoryProvider = ViewModelUtil.createFor(editorsPickViewModel)
//
//        val scenario =
//            launchFragmentInContainer(EditorsPicksFragmentArgs("").toBundle(), R.style.AppTheme) {
//                EditorsPicksFragment().apply {
//                    viewModelProviderFactory = mViewModelFactoryProvider
//                    databindingComponent = object : DataBindingComponent {
//                        override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
//                            return mockBindingAdapter
//                        }
//                    }
//                }
//            }
//
//        dataBindingIdlingResourceRule.monitorFragment(scenario)
//        scenario.onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), navController)
//        }
    }

    @Test
    fun loading() {
        editorsPickLiveData.postValue(Resource.loading(null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun error() {
        editorsPickLiveData.postValue(Resource.error("wtf", null))
        Espresso.onView(withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(withId(R.id.error_msg))
            .check(ViewAssertions.matches(ViewMatchers.withText("wtf")))
        Espresso.onView(withId(R.id.retry))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun loadingWithSimpleItem() {
//        val apodList = DataFactory.makeAPODList(10) //make a photo list
//        val apodItem = apodList[0] //get a single item from the list (first)
//
//        editorsPickLiveData.postValue(Resource.loading(apodItem))
//
//        Espresso.onView(ViewMatchers.withId(R.id.image_description))
//            .check(ViewAssertions.matches(ViewMatchers.withText(apodItem.explanation)))
//        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
//            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun loadedSimpleItem() {
//        val apodList = DataFactory.makeAPODList(10) //make a photo list
//        val apodItem = apodList[0] //get a single item from the list (first)
//
//        apodPhotoLiveData.postValue(Resource.success(apodItem))
//
//        Espresso.onView(ViewMatchers.withId(R.id.image_description))
//            .check(ViewAssertions.matches(ViewMatchers.withText(apodItem.explanation)))
//        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
//            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun clickCommentButton() {
//        val apodList = DataFactory.makeAPODList(1) //make a photo list
//        val apodItem = apodList[0] //get a single item from the list (first)
//
//        apodPhotoLiveData.postValue(Resource.success(apodItem))
//
//        onView(withId(R.id.comment_btn)).perform(click())
//
//        verify(navController).navigate(
//            ApodFragmentDirections.actionApodFragmentToCommentFragment(
//                apodItem.id, -1, anyString()
//            )
//        )
    }

    @Test
    fun likeButtonClicked() {

    }

}