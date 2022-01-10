package com.example.spaceexplorer.ui

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentBindingAdapters
import com.example.spaceexplorer.cache.model.APODWithComments
import com.example.spaceexplorer.util.*
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommentFragmentTest {

    private val navController = mock<NavController>()
    private lateinit var listViewModel: ListViewModel

    private var apodWithCommentsLiveData = MutableLiveData<Resource<APODWithComments>>()

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

        whenever(listViewModel.getAPODWithComments(any())).thenReturn(
            apodWithCommentsLiveData
        )

        val mViewModelFactoryProvider = ViewModelUtil.createFor(listViewModel)

        val scenario = launchFragmentInContainer(
            CommentFragmentArgs(-1L, 123L, "2015-03-03").toBundle(),
            R.style.AppTheme
        ) {
            CommentFragment().apply {
                viewModelProviderFactory = mViewModelFactoryProvider
                databindingComponent = object : DataBindingComponent {
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
    fun loading() {
        apodWithCommentsLiveData.postValue(Resource.loading(null))
        Espresso.onView(withId(R.id.progress_bar))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(withId(R.id.retry))
            .check(ViewAssertions.matches(not(isDisplayed())))
    }

    @Test
    fun error() {
        apodWithCommentsLiveData.postValue(Resource.error("wtf", null))
        Espresso.onView(withId(R.id.progress_bar))
            .check(ViewAssertions.matches(not(isDisplayed())))
        Espresso.onView(withId(R.id.error_msg))
            .check(ViewAssertions.matches(withText("wtf")))
        Espresso.onView(withId(R.id.retry))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun loadingWithSimpleItem() {

        val apodWithComments = InstrumentationTestUtil.createAPODWithCommentList()

        apodWithCommentsLiveData.postValue(Resource.loading(apodWithComments))

        onView(listMatcher().atPosition(0)).apply {
            check(
                ViewAssertions.matches(
                    hasDescendant(withText(apodWithComments.comments.get(0).comment))
                )
            )
        }

//        onView(withId(Rid.id.progress_bar)).check(matches(isDisplayed()))

    }

    @Test
    fun loadedSimpleItem() {

        val apodWithComments = InstrumentationTestUtil.createAPODWithCommentList()

        apodWithCommentsLiveData.postValue(Resource.success(apodWithComments))

        onView(listMatcher().atPosition(0)).apply {
            check(
                ViewAssertions.matches(
                    hasDescendant(withText(apodWithComments.comments.get(0).comment))
                )
            )
        }
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))

    }

    @Test
    fun removeComment() {

        val apodWithComments = InstrumentationTestUtil.createAPODWithCommentList()
        val commentList = apodWithComments.comments
        val lastComment = commentList[commentList.size - 1]
        apodWithCommentsLiveData.postValue(Resource.success(apodWithComments))

        val recyclerAction1 =
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(commentList.size - 1)
        onView(withId(R.id.comment_list)).perform(recyclerAction1)

        onView(listMatcher().atPosition(commentList.size - 1))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(commentList.size - 1)).apply {
            check(
                matches(
                    hasDescendant(withText(lastComment.comment))
                )
            )
        }

        //remove the last comment - and post the new LiveData, verify that the last item is not present in the list
        //scroll to position

        val newAPODWithComments =
            InstrumentationTestUtil.removeLastCommentFromAPOD(apodWithComments)//TestUtil2.removeLastComment(commentList)
        apodWithCommentsLiveData.postValue(Resource.success(newAPODWithComments))

        val newCommentListSize = newAPODWithComments.comments.size

        val recyclerAction2 =
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(newCommentListSize - 1)
        onView(withId(R.id.comment_list)).perform(recyclerAction2)

        onView(listMatcher().atPosition(newCommentListSize - 1))
            .check(matches(isDisplayed()))

        onView(listMatcher().atPosition(newCommentListSize - 1)).apply {
            check(
                matches(
                    hasDescendant(not(withText(lastComment.comment)))
                )
            )
        }

    }

    @Test
    fun appendComment() {

        val apodWithComments = InstrumentationTestUtil.createAPODWithCommentList()
        apodWithCommentsLiveData.postValue(Resource.success(apodWithComments))
        val commentList = apodWithComments.comments

        val lastComment = commentList[commentList.size - 1]
        apodWithCommentsLiveData.postValue(Resource.success(apodWithComments))

        val recyclerAction1 =
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(commentList.size - 1)
        onView(withId(R.id.comment_list)).perform(recyclerAction1)

        onView(listMatcher().atPosition(commentList.size - 1))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(commentList.size - 1)).apply {
            check(
                matches(
                    hasDescendant(withText(lastComment.comment))
                )
            )
        }

        //add a new comment and verify that it's that last thing that is added **

        val newApodWithComments = InstrumentationTestUtil.addCommentToAPODWithComments(apodWithComments)
        apodWithCommentsLiveData.postValue(Resource.success(newApodWithComments))
        val newCommentList = apodWithComments.comments

        val newLastComment = commentList[commentList.size - 1]

        val recyclerAction2 =
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(newCommentList.size - 1)
        onView(withId(R.id.comment_list)).perform(recyclerAction2)

        onView(listMatcher().atPosition(newCommentList.size - 1))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(newCommentList.size - 1)).apply {
            check(
                matches(
                    hasDescendant(withText(newLastComment.comment))
                )
            )
        }
    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.comment_list)

}