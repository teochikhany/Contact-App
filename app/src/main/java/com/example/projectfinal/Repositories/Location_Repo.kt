package com.example.projectfinal.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.projectfinal.Model.Location

@Dao
interface Location_Repo
{
    @Query("SELECT * FROM location")
    suspend fun getAll(): List<Location>

    @Insert
    suspend fun insert(loc: Location): Long

}