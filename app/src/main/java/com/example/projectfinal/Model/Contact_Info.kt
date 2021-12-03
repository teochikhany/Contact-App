package com.example.projectfinal.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_info")
data class Contact_Info(

    @ColumnInfo(name= "contact_id")
    @PrimaryKey(autoGenerate = true)
    val contact_id: Int,

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @ColumnInfo(name = "middle_name")
    val middleName: String?,

    @ColumnInfo(name = "job")
    val job: String?,

    @ColumnInfo(name = "phone_number")
    val phone_number: String?,

    @ColumnInfo(name = "email")
    val email: String?,

    @ColumnInfo(name = "custom_fields")
    val customFields: String?,

    // References
    @ColumnInfo(name = "organisation_id")
    val organisation_id: Long,

    @ColumnInfo(name = "location_id")
    val location_id: Long,
)

