package com.example.contact_app.Model

import android.annotation.SuppressLint
import android.provider.ContactsContract

import android.content.ContentProviderResult

import android.content.ContentProviderOperation
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import android.content.ContentResolver
import android.database.Cursor
import android.R.id
import androidx.core.database.getStringOrNull
import com.example.projectfinal.Model.Contact_Info


object contact_service
{
    lateinit var activity: AppCompatActivity

    fun Initialise(activity: AppCompatActivity)
    {
        this.activity = activity
    }

    fun addContact(given_name: String, name: String, mobile: String,  email: String) : Boolean
    {
        val contact = ArrayList<ContentProviderOperation>()
        contact.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )

        // first and last names
        contact.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, given_name).withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name).build()
        )

        // Contact Home phone
        contact.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobile).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME).build()
        )

        // Email    `
        contact.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Email.DATA, email).withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK).build()
        )

        return try
        {
            activity.contentResolver.applyBatch(ContactsContract.AUTHORITY, contact)
            true
        } catch (e: Exception)
        {
            Log.d("Error", "cannot save contact")
            false
        }

    }

    @SuppressLint("Range")
    fun read_contact() : List<Contact_Info>
    {
        val result: MutableList<Contact_Info> = mutableListOf()

        val cr: ContentResolver = activity.contentResolver
        val cur : Cursor = cr.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null
        )!!

        while (cur.moveToNext())
        {
            // get the id of the current contact
            val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))

            // check if that contact has a phone number
            val has_phone = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

            // placeholders
            var first = ""
            var last = ""
            var phone = ""
            var email: String? = ""
            // get the first and last name
            val nCur : Cursor = cr.query(
                ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = " + id, arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE ), ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)!!

            while (nCur.moveToNext())
            {
                first = nCur.getString(nCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                last = nCur.getString(nCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
            }

            nCur.close()

            // get the email
            val Ecur : Cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)!!

            while (Ecur.moveToNext())
            {
                email = Ecur.getString(Ecur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                if (email == null)
                    email = ""
            }
            Ecur.close()


            // get the phone number if it exists
            if (has_phone == 1)
            {
                val pCur : Cursor = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)!!

                while (pCur.moveToNext())
                {
                    phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    Log.d("phone", phone)
                }

                pCur.close()
            }

            result.add(Contact_Info(0, first, last, "", "", phone, email, "", 0, 0))
        }

        cur.close()

        return result
    }

}
