package com.example.projectfinal.Repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.projectfinal.Model.Organisation

@Dao
interface Organisation_Repo
{
    @Query("SELECT * FROM organisation")
    suspend fun getAll(): List<Organisation>

    @Query("SELECT * FROM organisation where organisation_name = (:name)")
    suspend fun getbyName(name: String): List<Organisation>

    @Query("SELECT * FROM organisation where organisationId = (:orgID)")
    suspend fun gebyID(orgID: Long): Organisation

    @Insert
    suspend fun insert(loc: Organisation) : Long
}