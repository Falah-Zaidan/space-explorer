package com.example.spaceexplorer.ui

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.viewmodels.FavouriteViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class FavouriteFragmentTest {

    private val navController = mock<NavController>()
    private lateinit var favouriteViewModel: FavouriteViewModel

    private var favouriteListLiveData = MutableLiveData<Resource<List<Favourite>>>()

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
        favouriteViewModel = mock()

        whenever(favouriteViewModel.getFavourites()).thenReturn(favouriteListLiveData)

        val mViewModelFactoryProvider = ViewModelUtil.createFor(favouriteViewModel)

        val scenario = launchFragmentInContainer(null, R.style.AppTheme) {
            FavouriteFragment().apply {
                viewModelProviderFactory = mViewModelFactoryProvider
                dataBindingComponent = object : DataBindingComponent {
                    override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                        return mockBindingAdapter
                    }
                }
                appExecutors = countingAppExecutors.appExecutors
            }
        }

        dataBindingIdlingResourceRule.monitorFragment(scenario)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun basicTest() {

    }

    @Test
    fun loading() {
        favouriteListLiveData.postValue(Resource.loading(null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun error() {
//        doNothing().`when`(listViewModel).retry()
        favouriteListLiveData.postValue(Resource.error("wtf", null))
        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.error_msg))
            .check(ViewAssertions.matches(ViewMatchers.withText("wtf")))
        Espresso.onView(ViewMatchers.withId(R.id.retry))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retry)).perform(ViewActions.click())
//        verify(favouriteViewModel).retry()
    }

    @Test
    fun loadingWithSimpleItem() {

    }

    @Test
    fun loadedSimpleItems() {

    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.favourite_list)

}