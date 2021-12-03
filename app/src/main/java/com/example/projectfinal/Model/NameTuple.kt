package com.example.projectfinal.Model

import androidx.room.ColumnInfo

data class NameTuple(
    @ColumnInfo(name = "contact_id")
    val contact_id: Int,

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?
)
