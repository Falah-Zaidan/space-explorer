package com.example.spaceexplorer.ui

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.util.DataBindingIdlingResourceRule
import com.example.spaceexplorer.util.Resource
import com.example.spaceexplorer.util.TaskExecutorWithIdlingResourceRule
import com.example.spaceexplorer.util.ViewModelUtil
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//class RoverImageDetailFragmentTest {
//
//    private val navController = mock<NavController>()
//    private lateinit var listViewModel: ListViewModel
//    private var photoLiveData = MutableLiveData<Resource<MarsRoverPhoto>>()
//
//    @Rule
//    @JvmField
//    val executorRule = TaskExecutorWithIdlingResourceRule()
//
//    @Rule
//    @JvmField
//    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()
//    private lateinit var mockBindingAdapter: FragmentBindingAdapters
//
//    @Before
//    fun init() {
//        mockBindingAdapter = mock()
//        listViewModel = mock()
//
//        whenever(listViewModel.getPhoto(any())).thenReturn(photoLiveData)
//        val mViewModelFactoryProvider = ViewModelUtil.createFor<ListViewModel>(listViewModel)
//
//        val scenario = launchFragmentInContainer(
//            RoverImageDetailFragmentArgs(123).toBundle(),
//            R.style.AppTheme
//        ) {
//            RoverImageDetailFragment().apply {
//                viewModelProviderFactory = mViewModelFactoryProvider
//                databindingComponent = object : DataBindingComponent {
//                    override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
//                        return mockBindingAdapter
//                    }
//                }
//            }
//        }
//
//        dataBindingIdlingResourceRule.monitorFragment(scenario)
//        scenario.onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), navController)
//        }
//    }
//
//    @Test
//    fun loading() {
//        photoLiveData.postValue(Resource.loading(null))
//        Espresso.onView(ViewMatchers.withId(R.id.progress_bar))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(ViewMatchers.withId(R.id.retry))
//            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
//    }
//
//    @Test
//    fun error() {
//
//    }
//
//}