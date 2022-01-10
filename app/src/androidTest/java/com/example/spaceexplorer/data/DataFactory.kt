package com.example.spaceexplorer.data

import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.model.MarsRoverPhoto
import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

class DataFactory {

    companion object Factory {

        fun makeAPODList(count: Int): List<APOD> {
            val simpleItems = mutableListOf<APOD>()
            repeat(count) {
                simpleItems.add(
                    makeAPOD()
                )
            }

            return simpleItems
        }

        fun makeAPOD(): APOD {
            return APOD(
                nextLong(),
                randomUuid(),
                randomUuid(),
                randomUuid()
            )
        }

        fun randomUuid(): String {
            return UUID.randomUUID().toString()
        }

        fun makePhotoList(count: Int): List<MarsRoverPhoto> {

            val photoList = arrayListOf<MarsRoverPhoto>()

            repeat(count) {
                photoList.add(
                    MarsRoverPhoto(
                        nextInt().toLong(),
                        randomUuid(),
                        randomUuid(),
                        createRover(),
                        createCamera()
                    )
                )
            }

            return photoList
        }

        private fun createRover(): MarsRoverPhoto.Rover {
            return MarsRoverPhoto.Rover(
                randomUuid()
            )
        }

        private fun createCamera(): MarsRoverPhoto.Camera {
            return MarsRoverPhoto.Camera(
                randomUuid()
            )
        }

    }

}