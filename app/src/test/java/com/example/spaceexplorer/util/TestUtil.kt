package com.example.spaceexplorer.util

import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.model.APODPhoto
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.remote.model.MarsRoverApiResponse
import java.util.*
import kotlin.random.Random.Default.nextLong

object TestUtil {

    fun createApod(date: String) = APOD(
        nextLong(),
        date,
        randomUuid(),
        randomUuid()
    )

    //used for network model... creating success calls
    fun createApodPhoto(date: String) = APODPhoto(
        date,
        randomUuid(),
        randomUuid()
    )

    fun createLikedAPOD(date: String): APOD {
        val apod = APOD(
            nextLong(),
            date,
            randomUuid(),
            randomUuid()
        )

        apod.favourite = true

        return apod
    }

    fun createMarsRoverApiResponse(): MarsRoverApiResponse {
        //create 10 MarsRoverPhotos within the ApiResponse
        val marsRoverPhotoList = mutableListOf<MarsRoverPhoto>()
        repeat(10) {
            marsRoverPhotoList.add(
                createMarsRoverPhoto()
            )
        }

        val marsRoverApiResponse = MarsRoverApiResponse(
            marsRoverPhotoList
        )

        return marsRoverApiResponse
    }

    fun createMarsRoverPhotos(count: Int): List<MarsRoverPhoto> {
        val photoList = mutableListOf<MarsRoverPhoto>()

        repeat(count) {
            photoList.add(createMarsRoverPhoto())
        }

        return photoList
    }

    fun createMarsRoverPhoto() = MarsRoverPhoto(
        id = nextLong(),
        image_href = randomPhotoUrl(),
        earth_date = randomUuid(),
        rover = createRover(),
        camera = createCamera()

    )

    fun randomUuid(): String {
        return UUID.randomUUID().toString()
    }

    fun randomPhotoUrl(): String {
        return "https://images.unsplash.com/photo-1481349518771-20055b2a7b24?ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8cmFuZG9tfGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&w=1000&q=80"
    }

    private fun createRover(): MarsRoverPhoto.Rover {
        return MarsRoverPhoto.Rover(randomUuid())
    }

    private fun createCamera(): MarsRoverPhoto.Camera {
        return MarsRoverPhoto.Camera(randomUuid())
    }

}
