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
class LoginDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertUserAndRetrieve() {
        //create list, insert it, retrieve it, then test each list item against the list before it was inserted
        val user = InstrumentationTestUtil.createUser()
        db.loginDao().insertUser(user)

        val loaded = db.loginDao().getUser().getOrAwaitValue()

        assertThat(loaded.name, `is`(user.name))
    }

}
