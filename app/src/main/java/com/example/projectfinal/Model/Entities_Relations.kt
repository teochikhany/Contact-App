package com.example.contact_app.Model

import android.provider.ContactsContract
import androidx.room.*
import com.example.projectfinal.Model.Contact_Info
import com.example.projectfinal.Model.Location
import com.example.projectfinal.Model.Organisation


data class ContactWithLocation(
    @Embedded val Contact: Contact_Info,

    @Relation(
        parentColumn = "location_id",
        entityColumn = "locationId"
    )

    val Location: Location
)


data class ContactWithOrganisation(
    @Embedded val Contact: Contact_Info,

    @Relation(
        parentColumn = "organisation_id",
        entityColumn = "organisationId"
    )

    val organisation: Organisation
)