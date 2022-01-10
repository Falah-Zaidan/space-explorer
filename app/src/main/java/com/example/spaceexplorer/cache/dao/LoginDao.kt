package com.example.spaceexplorer.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spaceexplorer.cache.model.User

@Dao
interface LoginDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User)

    @Query("SELECT * FROM user")
    fun getUser(): LiveData<User>

    @Query("DELETE FROM user")
    fun deleteUser()

}

