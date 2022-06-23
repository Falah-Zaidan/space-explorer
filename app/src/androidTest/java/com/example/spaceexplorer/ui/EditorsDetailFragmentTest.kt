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
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.data.DataFactory
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.viewmodels.EditorsPickViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditorsDetailFragmentTest {

    private val navController = mock<NavController>()
    private lateinit var editorsPickViewModel: EditorsPickViewModel

    private var editorPickPhotoLiveData = MutableLiveData<Resource<EditorsPickPhoto>>()

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
        editorsPickViewModel = mock()

        whenever(editorsPickViewModel.editorPickPhotoLiveData).thenReturn(
            editorPickPhotoLiveData
        )

        val mViewModelFactoryProvider = ViewModelUtil.createFor(editorsPickViewModel)

        val scenario =
            launchFragmentInContainer(
                SelectionDetailFragmentArgs("05-06-2022").toBundle(),
                R.style.AppTheme
            ) {
                SelectionDetailFragment().apply {
                    viewModelProviderFactory = mViewModelFactoryProvider
                    dataBindingComponent = object : DataBindingComponent {
                        override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                            return mockBindingAdapter
                        }
                    }
//                    appExecutors = countingAppExecutors.appExecutors
                }
            }

        dataBindingIdlingResourceRule.monitorFragment(scenario)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun loading() {
        editorPickPhotoLiveData.postValue(Resource.loading(null))
        Espresso.onView(withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.retry))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun error() {
        editorPickPhotoLiveData.postValue(Resource.error("wtf", null))
        Espresso.onView(withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(withId(R.id.error_msg))
            .check(ViewAssertions.matches(ViewMatchers.withText("wtf")))
        Espresso.onView(withId(R.id.retry))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun loadingWithSimpleItem() {
        val editorsPickList = DataFactory.makeEditorsPickList(10) //make a photo list
        val editorPickItem = editorsPickList[0] //get a single item from the list (first)

        editorPickPhotoLiveData.postValue(Resource.loading(editorPickItem))

        Espresso.onView(withId(R.id.image_explanation))
            .check(ViewAssertions.matches(ViewMatchers.withText(editorPickItem.explanation)))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun loadedSimpleItem() {
        val editorsPickList = DataFactory.makeEditorsPickList(10) //make a photo list
        val editorPickItem = editorsPickList[0] //get a single item from the list (first)

        editorPickPhotoLiveData.postValue(Resource.success(editorPickItem))

        Espresso.onView(withId(R.id.image_explanation))
            .check(ViewAssertions.matches(ViewMatchers.withText(editorPickItem.explanation)))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun clickCommentButton() {
        val editorsPickList = DataFactory.makeEditorsPickList(10) //make a photo list
        val editorPickItem = editorsPickList[0] //get a single item from the list (first)

        editorPickPhotoLiveData.postValue(Resource.success(editorPickItem))

        onView(withId(R.id.comment_btn)).perform(click())

        verify(navController).navigate(
            SelectionDetailFragmentDirections.actionSelectionDetailFragmentToCommentFragment(
                -1, -1, "", -1
            )
        )
    }

    @Test
    fun likeButtonClicked() {

        //this is to imitate what happens with the data when the user clicks the like border...

        val editorPickItem = DataFactory.createEditorPickPhotoFavourite()
        editorPickPhotoLiveData.postValue(Resource.success(editorPickItem))

        //check that filled like is displayed
        onView(withId(R.id.favourite_filled))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //check that border like is not displayed
        onView(withId(R.id.favourite_border))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
    }

}