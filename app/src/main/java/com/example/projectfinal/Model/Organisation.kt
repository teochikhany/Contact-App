package com.example.projectfinal.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = arrayOf(Index(value = ["organisation_name"], unique = true)))
data class Organisation(
    @PrimaryKey(autoGenerate = true)
    val organisationId: Long,

    @ColumnInfo(name = "organisation_name")
    val name: String
)
