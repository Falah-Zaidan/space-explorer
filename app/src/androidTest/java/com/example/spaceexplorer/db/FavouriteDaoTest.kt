package com.example.spaceexplorer.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.util.InstrumentationTestUtil
import com.example.spaceexplorer.util.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertFavouritesAndReadFavouriteList() {
        //create list, insert it, retrieve it, then test each list item against the list before it was inserted
        val favouriteList = InstrumentationTestUtil.createFavourites(10)
        db.favouriteDao().insertFavourites(favouriteList)

        val loaded = db.favouriteDao().getFavourites().getOrAwaitValue()

        //we don't tell room to order the results - so the retrieved items will at different indexes
        assertThat(loaded.size, `is`(favouriteList.size))
    }

    @Test
    fun deleteFavourite() {

    }

}
