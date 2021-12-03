package com.example.projectfinal.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.contact_app.Model.contact_service
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.R
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import android.util.Patterns
import com.example.projectfinal.Model.Contact_Info
import com.example.projectfinal.Model.Organisation
import java.util.regex.Pattern
import com.google.zxing.integration.android.IntentIntegrator
import android.content.Intent
import org.json.JSONArray


class PlusFragment : Fragment()
{
    val dynamic_fields: MutableList<EditText> = mutableListOf()
    lateinit var myView: View

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View?
    { // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_plus, container, false)

        val add_fields = view.findViewById<Button>(R.id.add_fields_button)
        val save_contact = view.findViewById<Button>(R.id.save_contact_button)
        val read_qr = view.findViewById<Button>(R.id.read_qr_button)

        add_fields.setOnClickListener{ add_fields(view) }
        save_contact.setOnClickListener{ save_contact(view) }
        read_qr.setOnClickListener{ read_qr() }

        myView = view

        return view
    }

    private fun save_contact(view: View)
    = runBlocking {

        // get all the variable from the view
        val first_name = view.findViewById<EditText>(R.id.first_name_input).text
        val last_name = view.findViewById<EditText>(R.id.last_name_input).text
        val middle_name = view.findViewById<EditText>(R.id.middle_name_input).text
        val organisation_name = view.findViewById<EditText>(R.id.organisation_input).text
        val job_title = view.findViewById<EditText>(R.id.job_input).text
        val phone = view.findViewById<EditText>(R.id.phone_input).text
        val email = view.findViewById<EditText>(R.id.email_input).text

        if (first_name.toString() == "")
        {
            Toast.makeText(context, "First Name cannot be empty", Toast.LENGTH_SHORT).show()
            return@runBlocking
        }

        if (email.toString() != "" && !invalidEmail(email.toString()))
        {
            Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return@runBlocking
        }

        // get the db
        val db = Contact_database.getDB(view.context)

        val contactDao = db.contactDao()
        val locationDao = db.locationDao()
        val organisationDoa = db.organisationDao()

        // val loc = Location(0, "beirut")
        // locationDao.insert(loc)

        // check if organisation is already present
        val org = Organisation(0, organisation_name.toString().lowercase())

        val temp = organisationDoa.getbyName(organisation_name.toString().lowercase())

        val org_id: Long = if (temp.isNotEmpty())
        {
            temp[0].organisationId
        }
        else
        {
            organisationDoa.insert(org)
        }


        // read dynamic fields
        val json_string = JSONObject()
        for (elem in dynamic_fields)
        {
            json_string.put(elem.hint.toString(), elem.text.toString())
        }

        // add the contact to the database
        val new_user = Contact_Info(0,
            first_name.toString().lowercase(),
            last_name.toString().lowercase(),
            middle_name.toString().lowercase(),
            job_title.toString().lowercase(),
            phone.toString().lowercase(),
            email.toString().lowercase(),
            json_string.toString(),
            org_id,
            1)

        // save the contact to our own db
        contactDao.insert(new_user)

        // save the contact to the phone contact db
        contact_service.addContact(
            first_name.toString(),
            last_name.toString(),
            phone.toString(),
            email.toString())


        // contactDao.insert(new_user)

        // print_contact(contactDao)
        // print_organisation(organisationDoa)

        first_name.clear()
        last_name.clear()
        middle_name.clear()
        organisation_name.clear()
        job_title.clear()
        phone.clear()
        email.clear()

        for (elem in dynamic_fields)
        {
            elem.text.clear()
        }

    }


    private fun add_fields(view: View)
    {
        val vertical_list = view.findViewById<LinearLayout>(R.id.fields_vertical_list)
        val edit_text = EditText(view.context)

        ask_name(view, edit_text)
        vertical_list.addView(edit_text)
        dynamic_fields.add(edit_text)
    }

    private fun ask_name(view: View, edit_text: EditText)
    {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Field Name")
        val dialogLayout = layoutInflater.inflate(R.layout.activity_pop_up, null)
        val input_field_name  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ -> edit_text.hint = input_field_name.text }
        builder.show()
    }


//    private fun print_contact(contactDao: Contact_Repo)
//    = runBlocking {
//
//        val users: List<Contact_Info> = contactDao.getAll()
//        Log.d("merhy", users.toString())
//    }
//
//    private fun print_organisation(organisationDao: Organisation_Repo)
//    = runBlocking {
//
//        val users: List<Organisation> = organisationDao.getAll()
//        Log.d("merhy", users.toString())
//    }

    private fun invalidEmail(email: String): Boolean
    {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun read_qr()
    {
        val intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code")
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null)
        {
            if (result.contents == null)
            {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            } else
            {
                Toast.makeText(context, "Scanned : " + result.contents, Toast.LENGTH_LONG).show()

                val first_name = myView.findViewById<EditText>(R.id.first_name_input)
                val last_name = myView.findViewById<EditText>(R.id.last_name_input)
                val middle_name = myView.findViewById<EditText>(R.id.middle_name_input)
                val organisation_name = myView.findViewById<EditText>(R.id.organisation_input)
                val job_title = myView.findViewById<EditText>(R.id.job_input)
                val phone = myView.findViewById<EditText>(R.id.phone_input)
                val email = myView.findViewById<EditText>(R.id.email_input)

                val json_result = JSONObject(result.contents)

                first_name.setText(json_result.getString("fn"))
                last_name.setText(json_result.getString("ln"))
                middle_name.setText(json_result.getString("mn"))
                organisation_name.setText(json_result.getString("org"))
                job_title.setText(json_result.getString("jb"))
                phone.setText(json_result.getString("pn"))
                email.setText(json_result.getString("em"))

                val custom_fields = json_result.getString("cf")
                val custom_fields_array = JSONArray(custom_fields)

                for (i in 0 until custom_fields_array.length())
                {
                    val fields_object = custom_fields_array.getJSONObject(i)
                    val keys: JSONArray = fields_object.names()!!

                    for (j in 0 until keys.length())
                    {
                        val key = keys.getString(j)
                        val value: String = fields_object.getString(key)

                        val vertical_list = myView.findViewById<LinearLayout>(R.id.fields_vertical_list)
                        val edit_text = EditText(myView.context)
                        edit_text.hint = key
                        edit_text.setText(value)
                        vertical_list.addView(edit_text)
                        dynamic_fields.add(edit_text)
                    }
                }
            }
        }
    }

//    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
//    {
//        super.onActivityResult(requestCode, resultCode, data)
//    }
}