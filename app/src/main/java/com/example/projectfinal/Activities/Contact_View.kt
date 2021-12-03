package com.example.projectfinal.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.Fragments.homeFragment
import com.example.projectfinal.Model.Contact_Info
import com.example.projectfinal.Model.NameTuple
import com.example.projectfinal.Model.Organisation
import com.example.projectfinal.R
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject


class Contact_View : AppCompatActivity()
{
    val dynamic_fields: MutableList<EditText> = mutableListOf()
    private var edit_texts = mutableListOf<EditText>()
    private var inEditMode = false
    private var ID : Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_view)

        val first_name = findViewById<EditText>(R.id.first_name_output)
        val last_name = findViewById<EditText>(R.id.last_name_output)
        val middle_name = findViewById<EditText>(R.id.middle_name_output)
        val organisation_name = findViewById<EditText>(R.id.organisation_output)
        val job_title = findViewById<EditText>(R.id.job_output)
        val phone = findViewById<EditText>(R.id.phone_output)
        val email = findViewById<EditText>(R.id.email_output)

        val edit_button = findViewById<Button>(R.id.edit_fields_button)
        edit_button.setOnClickListener{ editButton() }

        val save_button = findViewById<Button>(R.id.save_contact_edit_button)
        save_button.setOnClickListener{ saveButton() }

        val qr_button = findViewById<Button>(R.id.generate_qr_code_button)
        qr_button.setOnClickListener{ generat_qr_code() }

        edit_texts.add(first_name)
        edit_texts.add(middle_name)
        edit_texts.add(last_name)
        edit_texts.add(organisation_name)
        edit_texts.add(job_title)
        edit_texts.add(phone)
        edit_texts.add(email)

        ID = intent.getStringExtra("contactID")!!.toInt()
        val contact: Contact_Info = getContact(ID)
        first_name.setText(contact.firstName)
        last_name.setText(contact.lastName)
        middle_name.setText(contact.middleName)
        organisation_name.setText(getOranisation(contact.organisation_id).name)
        job_title.setText(contact.job)
        phone.setText(contact.phone_number)
        email.setText(contact.email)


        // get the dynamic fields
        val list_of_fields = findViewById<LinearLayout>(R.id.contact_fields_vertical_list)
        val json_obj = JSONObject(contact.customFields!!)
        val json_keys = json_obj.names() ?: return

        for (i in 0 until json_keys.length())
        {
            val key = json_keys[i] as String
            val value = json_obj[key] as String

            val edit_text = EditText(this@Contact_View)
            edit_text.hint = key
            edit_text.setText(value)
            edit_text.focusable = View.NOT_FOCUSABLE

            list_of_fields.addView(edit_text)

            dynamic_fields.add(edit_text)
        }

    }



    private fun getContact(contact_id: Int): Contact_Info
    = runBlocking {
        val contactDao = Contact_database.getDB(this@Contact_View).contactDao()

        return@runBlocking contactDao.getFulContact(contact_id)
    }

    private fun getOranisation(organisation_id: Long): Organisation
    = runBlocking {
        val organisationDAO = Contact_database.getDB(this@Contact_View).organisationDao()
        Log.d("org id", organisation_id.toString())
        return@runBlocking organisationDAO.gebyID(organisation_id)
    }

    private fun generat_qr_code()
    {
        val intent = Intent(this, QrCodeActivity::class.java)

        val json_contact = JSONObject()
        json_contact.put("fn", edit_texts[0].text.toString())
        json_contact.put("ln", edit_texts[2].text.toString())
        json_contact.put("mn", edit_texts[1].text.toString())
        json_contact.put("pn", edit_texts[5].text.toString())
        json_contact.put("em", edit_texts[6].text.toString())
        json_contact.put("org", edit_texts[3].text.toString())
        json_contact.put("jb", edit_texts[4].text.toString())

        val custom_array = JSONArray()
        for (elem in dynamic_fields)
        {
            val j_object = JSONObject()
            j_object.put(elem.hint.toString(), elem.text.toString())

            custom_array.put(j_object)
        }

        json_contact.put("cf", custom_array)

        intent.putExtra("json_contact", json_contact.toString())
        startActivity(intent)
    }


    private fun editButton()
    {
        val edit_button = findViewById<Button>(R.id.edit_fields_button)

        if (inEditMode)
        {
            edit_button.text = getString(R.string.start_edit)
            for (i in 0 until edit_texts.size)
            {
                edit_texts[i].focusable = View.NOT_FOCUSABLE
            }
            for (i in 0 until dynamic_fields.size)
            {
                dynamic_fields[i].focusable = View.NOT_FOCUSABLE
            }
            inEditMode = false
        }
        else
        {
            edit_button.text = getString(R.string.stop_edit)
            for (i in 0 until edit_texts.size)
            {
                edit_texts[i].focusable = View.FOCUSABLE
                edit_texts[i].isFocusableInTouchMode = true
            }
            for (i in 0 until dynamic_fields.size)
            {
                dynamic_fields[i].focusable = View.FOCUSABLE
                dynamic_fields[i].isFocusableInTouchMode = true
            }
            inEditMode = true
        }

    }

    private fun saveButton()
    {
        runBlocking {
            // get the dabases
            val contactDAO = Contact_database.getDB(this@Contact_View).contactDao()
            val organisationDAO = Contact_database.getDB(this@Contact_View).organisationDao()

            val json_string_dyn = JSONObject()
            for (elem in dynamic_fields)
            {
                json_string_dyn.put(elem.hint.toString(), elem.text.toString())
            }

            contactDAO.UpdateFirstName(ID, edit_texts[0].text.toString())
            contactDAO.UpdateMiddleName(ID, edit_texts[1].text.toString())
            contactDAO.UpdateLastName(ID, edit_texts[2].text.toString())

            // check if organisation is already present
            val org = Organisation(0, edit_texts[3].text.toString().lowercase())

            val temp = organisationDAO.getbyName(edit_texts[3].text.toString().lowercase())

            val org_id: Long = if (temp.isNotEmpty())
            {
                temp[0].organisationId
            }
            else
            {
                organisationDAO.insert(org)
            }
            ////////

            contactDAO.UpdateOrgID(ID, org_id)
            contactDAO.UpdateJOb(ID, edit_texts[4].text.toString())
            contactDAO.UpdatePhone(ID, edit_texts[5].text.toString())
            contactDAO.UpdateEmail(ID, edit_texts[6].text.toString())
            contactDAO.UpdateCustom(ID, json_string_dyn.toString())

            // notify the recyclerView of a change
            homeFragment.contactAdapter.ItemChange(NameTuple(ID,edit_texts[0].text.toString(),edit_texts[2].text.toString() )
                ,intent.getIntExtra("position", 0))

        }

        // to close the current activity automatically
        finish()

    }


}