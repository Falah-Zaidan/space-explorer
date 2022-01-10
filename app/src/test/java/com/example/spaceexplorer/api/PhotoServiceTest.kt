package com.example.spaceexplorer.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.spaceexplorer.remote.PhotoService
import com.example.spaceexplorer.util.ApiSuccessResponse
import com.example.spaceexplorer.util.LiveDataCallAdapterFactory
import com.example.spaceexplorer.util.getOrAwaitValue2
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class PhotoServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: PhotoService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(PhotoService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun testMarsRoverApiResponse() {
        enqueueResponse("rover-response.json")
        val roverResponse = service.searchPhotosByDate(
            "curiosity",
            "key",
            "2015-07-03",
            1
        ).getOrAwaitValue2()

        val roverResponseBody = (roverResponse as ApiSuccessResponse).body
        val request = mockWebServer.takeRequest()
        assertThat(
            request.path,
            `is`("/mars-photos/api/v1/rovers/curiosity/photos?api_key=key&earth_date=2015-07-03&page=1")
        )
        assertThat(roverResponse, notNullValue())

        val marsRoverPhoto = roverResponseBody.marsRoverPhotos.get(0)
        val value: Long = 423171
        assertThat(marsRoverPhoto.id.toInt(), `is`(value.toInt()))
        assertThat(
            marsRoverPhoto.image_href,
            `is`("http://mars.jpl.nasa.gov/msl-raw-images/msss/01033/mcam/1033ML0045210040305609E01_DXXX.jpg")
        )
        assertThat(marsRoverPhoto.earth_date, `is`("2015-07-03"))
    }

    @Test
    fun testAPODResponse() {
        enqueueResponse("apod-response.json")
        val apodResponse = service.getAstronomyPictureOfTheDay(
            "2015-07-03",
            "5vv4KVOuKmLQ852nlkS2WTpjofWpcjnAj4yj1NdO"
        ).getOrAwaitValue2()

        val apodResponseBody = (apodResponse as ApiSuccessResponse).body
        val request = mockWebServer.takeRequest()
        assertThat(
            request.path,
            `is`("/planetary/apod?date=2015-07-03&api_key=5vv4KVOuKmLQ852nlkS2WTpjofWpcjnAj4yj1NdO")
        )

        assertThat(apodResponseBody, notNullValue())

        assertThat(
            apodResponseBody.url,
            `is`("https://apod.nasa.gov/apod/image/2108/PerseusFireball_Dandan_960.jpg")
        )
        assertThat(apodResponseBody.date, `is`("2021-08-03"))
        assertThat(
            apodResponseBody.explanation,
            `is`("It was bright and green and flashed as it moved quickly along the Milky Way. It left a trail that took 30 minutes to dissipate.  Given the day, August 12, and the direction, away from Perseus, it was likely a small bit from the nucleus of Comet Swift-Tuttle plowing through the Earth's atmosphere -- and therefore part of the annual Perseids meteor shower.  The astrophotographer captured the fireball as it shot across the sky in 2018 above a valley in Yichang, Hubei, China. The meteor's streak, also caught on video, ended near the direction of Mars on the lower left. Next week, the 2021 Perseids meteor shower will peak again.  This year the Moon will set shortly after the Sun, leaving a night sky ideal for seeing lots of Perseids from dark and clear locations across planet Earth.   Follow APOD in English on: Facebook,  Instagram, or Twitter")
        )
    }

    //makes a mockWebSever with the headers
    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        //reference the file location in the project
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
        //inputSteam is null here
        val source = inputStream.source().buffer()
        //Create a mock response
        val mockResponse = MockResponse()
        //Add in the headers to the mockResponse
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        //set the body of the mockResponse to the JSON file that you read as a stream
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}
