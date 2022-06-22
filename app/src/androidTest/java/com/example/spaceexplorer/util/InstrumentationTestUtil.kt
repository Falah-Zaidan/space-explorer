package com.example.spaceexplorer.util

import com.example.spaceexplorer.cache.model.*
import com.example.spaceexplorer.model.APODPhoto
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.remote.model.MarsRoverApiResponse
import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

//why use a Kotlin object vs a companion object in a regular class?
object InstrumentationTestUtil {

    fun createApodPhoto() = APODPhoto(
        date = randomUuid(),
        explanation = randomUuid(),
        url = randomPhotoUrl()
    )

    fun createCacheApod() = APOD(
        nextLong(),
        randomUuid(),
        randomUuid(),
        randomUuid()
    )

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

    fun createMarsRoverApiResult(): MarsRoverApiResult {
        return MarsRoverApiResult(
            nextInt()
        )
    }

    fun createUser(): User {
        return User(
            nextInt(),
            randomUuid(),
            randomUuid()
        )
    }

    fun removeLastCommentFromAPOD(apodWithComments: APODWithComments): APODWithComments {
        val commentList = apodWithComments.comments

        val updatedCommentList = removeLastComment(commentList)

        return APODWithComments(
            apodWithComments.apod,
            updatedCommentList
        )
    }

    fun addCommentToAPODWithComments(apodWithComments: APODWithComments): APODWithComments {
        val commentList = apodWithComments.comments

        val updatedCommentList = addNewComment(commentList)

        return APODWithComments(
            apodWithComments.apod,
            updatedCommentList
        )
    }

    fun removeLastComment(commentList: List<Comment>): List<Comment> {

        val newCommentList = arrayListOf<Comment>()

        commentList.forEach {
            newCommentList.add(it)
        }

        newCommentList.removeAt(commentList.size - 1)

        return newCommentList
    }

    fun addComment(commentList: List<Comment>, comment: Comment): List<Comment> {
        val newCommentList = arrayListOf<Comment>()

        commentList.forEach {
            newCommentList.add(it)
        }

        newCommentList.add(comment)

        return newCommentList

    }

    fun addNewComment(commentList: List<Comment>): List<Comment> {
        val newCommentList = arrayListOf<Comment>()

        commentList.forEach {
            newCommentList.add(it)
        }

        //get the apodId that these comments are associated with to keep this relation
        val commentApodId = commentList.get(0).apodId

        val newComment = Comment(
            nextInt(),
            commentApodId,
            nextInt().toLong(),
            randomUuid(),
            randomUuid(),
            nextInt(),
            randomUuid(),
            "https://picsum.photos/200/300"
        )

        newCommentList.add(newComment)

        return newCommentList
    }

    fun createAPODWithCommentList(): APODWithComments {

        val apodId = nextInt().toLong()

        return APODWithComments(
            createApod(apodId),
            createCommentList(apodId)
        )
    }

    private fun createApod(id: Long): APOD {
        return APOD(
            id,
            randomUuid(),
            randomUuid(),
            randomUuid()
        )
    }

    private fun createCommentList(apodId: Long): List<Comment> {
        val commentList = arrayListOf<Comment>()

        repeat(20) {
            commentList.add(
                Comment(
                    nextInt(),
                    apodId,
                    nextInt().toLong(),
                    randomUuid(),
                    randomUuid(),
                    nextInt(),
                    randomUuid(),
                    "https://picsum.photos/200/300" // profile picture
                )
            )
        }

        return commentList
    }

    fun createFavourites(count: Int): List<Favourite> {
        val favourites = arrayListOf<Favourite>()

        repeat(count) {
            favourites.add(
                Favourite(
                    nextInt().toLong(),
                    randomUuid(),
                    randomUuid(),
                    randomUuid(),
                    randomUuid(),
                    randomUuid(),
                    randomUuid(),
                    randomUuid(),
                    randomUuid()
                )
            )
        }

        return favourites
    }
}
