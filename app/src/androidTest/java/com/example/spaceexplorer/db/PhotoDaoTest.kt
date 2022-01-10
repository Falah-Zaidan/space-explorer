package com.example.spaceexplorer.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spaceexplorer.util.InstrumentationTestUtil
import com.example.spaceexplorer.util.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertMarsRoverPhotosAndRead() {
        //create a list of photos
        val marsRoverPhotos = InstrumentationTestUtil.createMarsRoverPhotos(10)

        //insert into DB using a Dao operation
        db.photoDao().insertPhotos(marsRoverPhotos)

        //get the value of the id of the first photo you inserted
        val marsRoverPhotoListItem = marsRoverPhotos[0]
        val loaded = db.photoDao().getPhoto(marsRoverPhotoListItem.id).getOrAwaitValue()

        //check verify correct values
        assertThat(loaded, notNullValue())
        assertThat(loaded.earth_date, `is`(marsRoverPhotoListItem.earth_date))
        assertThat(loaded.image_href, `is`(marsRoverPhotoListItem.image_href))
        assertThat(loaded.camera, notNullValue())
        assertThat(loaded.rover, notNullValue())
    }

    @Test
    fun insertMarsRoverApiResultAndRead() {
        val marsRoverApiResult = InstrumentationTestUtil.createMarsRoverApiResult()

        db.photoDao().insertMarsRoverApiResult(marsRoverApiResult)

        val loaded = db.photoDao().getMarsRoverApiResult()

        val loadedId = loaded.get(0).next

        assertThat(loaded, notNullValue())
        assertThat(loaded.get(0).next, `is`(loadedId))

    }

    @Test
    fun insertAPODWithCommentAndReadRelation() {

        //create an APODWithComment, insert into DB separately (apod, then commentList), retrieve from DB, check values match

        val apodWithComment = InstrumentationTestUtil.createAPODWithCommentList()
        val apod = apodWithComment.apod
        val commentList = apodWithComment.comments

        db.photoDao().insertAPOD(apod)
        db.photoDao().insertComments(commentList)

        val loaded = db.photoDao().getAPODWithComments(apod.id).getOrAwaitValue()
        assertThat(loaded, notNullValue())

        val loadedItemApodId = loaded.apod.id

        assertThat(loadedItemApodId, `is`(apod.id))

        //to be expected since room does not order them (we've not told it to order the retrieved results)
        assertThat(loaded.comments.size, `is`(commentList.size))
    }

}
