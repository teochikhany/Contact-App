package com.example.projectfinal.Databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projectfinal.Model.Contact_Info
import com.example.projectfinal.Model.Location
import com.example.projectfinal.Model.Organisation
import com.example.projectfinal.Repositories.Contact_Repo
import com.example.projectfinal.Repositories.Location_Repo
import com.example.projectfinal.Repositories.Organisation_Repo


// make this a singletion
@Database(entities = arrayOf(Contact_Info::class, Location::class, Organisation::class),
    version = 1)

abstract class Contact_database : RoomDatabase()
{
    abstract fun contactDao(): Contact_Repo
    abstract fun locationDao(): Location_Repo
    abstract fun organisationDao(): Organisation_Repo

    companion object
    {
        @JvmStatic
        private var instance: Contact_database? = null

        @JvmStatic
        fun getDB(context: Context): Contact_database
        {

            if (instance == null)
            {
                instance = Room.databaseBuilder(
                            context, Contact_database::class.java, "first_test_13"
                            ).build()
            }

            return instance!!
        }

        @JvmStatic
        fun destroyInstance() {

            if (instance?.isOpen == true) {
                instance?.close()
            }

            instance = null
        }
    }
}