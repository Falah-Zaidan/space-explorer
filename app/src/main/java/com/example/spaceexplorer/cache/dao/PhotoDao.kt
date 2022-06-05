package com.example.spaceexplorer.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.spaceexplorer.cache.db.Constants
import com.example.spaceexplorer.cache.model.*
import com.example.spaceexplorer.model.APODPhoto
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto

@Dao
interface PhotoDao {

    //EditorPickPhoto related operations
    @Query("SELECT * FROM editorspickphoto")
    fun getEditorPickPhotos(): LiveData<List<EditorsPickPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEditorPickPhotos(editorPickPhotos: List<EditorsPickPhoto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEditorPickPhoto(editorsPickPhoto: EditorsPickPhoto)

    @Query("DELETE FROM EditorsPickPhoto")
    fun deleteEditorsPickEntries()

    //END EditorPickPhoto related operations

//   MarsRoverPhoto related operations

    @Query("SELECT * FROM marsRoverPhotos")
    fun getPhotos(): LiveData<List<MarsRoverPhoto>>

    @Query("SELECT * FROM marsRoverPhotos WHERE rover_name = :roverName AND earth_date = :earth_date ORDER BY id ASC")
    fun getPhotosByRoverNameAndDate(
        roverName: String,
        earth_date: String
    ): LiveData<List<MarsRoverPhoto>>

    @Query("SELECT * FROM marsRoverPhotos WHERE marsRoverPhotos.id = :id")
    fun getPhoto(id: Long): LiveData<MarsRoverPhoto>

    //This gets the SearchResult (which is the page from the API)
    @Query("SELECT * FROM mars_rover_api_result")
    fun getMarsRoverApiResult(): List<MarsRoverApiResult>

    //INSERT OPERATIONS
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPhotos(marsRoverPhotos: List<MarsRoverPhoto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarsRoverApiResult(result: MarsRoverApiResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMarsRoverPhoto(marsRoverPhoto: MarsRoverPhoto)

    @Query("SELECT * FROM favourite")
    fun getFavouritePhotos(): LiveData<List<Favourite>>

    @Update
    fun updateMarsRoverPhoto(marsRoverPhoto: MarsRoverPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouritePhoto(favouritePhotos: List<Favourite>)

    @Query("SELECT * FROM APODPhotos WHERE favourite = 'true'")
    fun getFavouriteAPODPhotos(): LiveData<List<APODPhoto>>

    @Query(Constants.DELETE_ALL_PHOTOS)
    suspend fun clearPhotos()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(comments: List<Comment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: Comment)

    @Query("DELETE FROM comment WHERE comment.userCreatorId = :userCreatorId")
    fun deleteComment(userCreatorId: Int)

    @Transaction
    @Query("SELECT * FROM APOD WHERE id = :apodId ORDER BY id ASC")
    fun getAPODWithComments(apodId: Long): LiveData<APODWithComments>

    @Transaction
    @Query("SELECT * FROM marsRoverPhotos WHERE id = :marsRoverPhotoId ORDER BY id ASC")
    fun getMarsRoverPhotoWithComments(marsRoverPhotoId: Int): LiveData<MarsRoverPhotoWithComments>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAPOD(apod: APOD)

    @Query("SELECT * FROM APODPhotos WHERE date = :date")
    fun getAPODPhoto(date: String): LiveData<APODPhoto>

    @Query("SELECT * FROM APOD WHERE date = :date")
    fun getAPOD(date: String): LiveData<APOD>

    @Query("SELECT * FROM APOD WHERE id = :id")
    fun getAPODById(id: Int): APOD

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAPODPhoto(photo: APODPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCacheApodPhoto(cacheApodPhoto: APOD)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM CurrentAPOD")
    suspend fun getCurrentAPOD(): CurrentAPOD

    @Query("SELECT * FROM CurrentAPOD")
    fun getCurrentAPODStandard(): CurrentAPOD

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentAPOD(currentAPOD: CurrentAPOD)

    @Query("DELETE FROM CurrentAPOD")
    fun deleteAPODEntries()

}

