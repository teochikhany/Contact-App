package com.example.projectfinal.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val locationId: Long,

    @ColumnInfo(name = "location_name")
    val name: String
)
