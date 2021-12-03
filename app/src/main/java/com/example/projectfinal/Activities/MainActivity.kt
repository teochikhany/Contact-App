package com.example.projectfinal.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.contact_app.Model.contact_service
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.Fragments.AccountFragment
import com.example.projectfinal.Fragments.PlusFragment
import com.example.projectfinal.Fragments.homeFragment
import com.example.projectfinal.Model.NameTuple
import com.example.projectfinal.Helpers.NetworkQueue
import com.example.projectfinal.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.runBlocking
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior:  BottomSheetBehavior<View>
    private val dynamic_fields: MutableList<EditText> = mutableListOf()

    override fun onCreate(savedInstanceState:Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        setCurrentFragment(homeFragment())

        contactPermission()

        val bottomSheetBehaviorView = findViewById<View>(R.id.filter_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBehaviorView)

        val floating_filter = findViewById<FloatingActionButton>(R.id.advanced_filter)

        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_nav -> {
                    setCurrentFragment(homeFragment())
                    floating_filter.show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                R.id.plus_nav -> {
                    setCurrentFragment(PlusFragment())
                    floating_filter.hide()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                R.id.account_nav -> {
                    setCurrentFragment(AccountFragment())
                    floating_filter.hide()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            true
        }


        // val floatingActionButton = findViewById<FloatingActionButton>(R.id.advanced_filter)

        floating_filter.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                floating_filter.setImageResource(R.drawable.close_icon)
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                floating_filter.setImageResource(R.drawable.filter_icon)
            }
        }
        contact_service.Initialise(this)

        val filter_button = findViewById<Button>(R.id.save_contact_button_filter)
        filter_button.setOnClickListener{filterButton()}

        val add_fields_button = findViewById<Button>(R.id.add_fields_button_filter)
        add_fields_button.setOnClickListener{add_fields()}

        // NetworkQueue.InitialiseQueue(this)
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_view, fragment)
            commit()
        }

    private fun contactPermission()
    {
        val permission: String = Manifest.permission.WRITE_CONTACTS
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), 1)
        }
    }


    private fun filterButton()
    = runBlocking {

        // get all the static fields
        val first_name = findViewById<EditText>(R.id.first_name_filter).text.toString()
        val last_name = findViewById<EditText>(R.id.last_name_filter).text.toString()
        val middle_name = findViewById<EditText>(R.id.middle_name_filter).text.toString()
        val organisation_name = findViewById<EditText>(R.id.organisation_filter).text.toString()
        val job_title = findViewById<EditText>(R.id.job_filter).text.toString()
        val phone = findViewById<EditText>(R.id.phone_filter).text.toString()
        val email = findViewById<EditText>(R.id.email_filter).text.toString()


        // get the db
        val db = Contact_database.getDB(this@MainActivity)
        val contactDao = db.contactDao()
        val orgDao = db.organisationDao()


        // get the organisation name
        val allOrgs = orgDao.getbyName(organisation_name)
        var orgID: Long = 0

        if (allOrgs.isNotEmpty())
            orgID = allOrgs[0].organisationId


        // prep the array of arguments
        val arary_of_arguments = arrayListOf(first_name, last_name, middle_name, job_title, phone, email, orgID)

        // make the query
        var QUERY = "SELECT contact_id, first_name, last_name FROM contact_info where first_name LIKE '%' || ? || '%' and last_name LIKE '%' || ? || '%' and middle_name LIKE '%' || ? || '%' and job LIKE '%' || ? || '%' and phone_number LIKE '%' || ? || '%' and email LIKE '%' || ? || '%' and organisation_id = ?"


        // get the dynamics fields
        for(i in 0 until dynamic_fields.size)
        {
            if (i == 0)
                QUERY += " and "

            val elem = dynamic_fields[i]
            QUERY += "custom_fields LIKE ?"
            arary_of_arguments.add("%" + elem.hint.toString() + "%" + elem.text.toString() + "%")

            if (i != dynamic_fields.size - 1)
                QUERY += " and "
        }

        // Log.d("query", QUERY)
        // Log.d("query array", arary_of_arguments.toString())

        // get the result of the filter
        //val result: List<NameTuple> = contactDao.getFulContactFilter(first_name, last_name, middle_name, job_title, phone, email, orgID, dynamic_fields_query)
        val result: List<NameTuple> = contactDao.getFulContactFilter2(SimpleSQLiteQuery(QUERY, arary_of_arguments.toArray()) )

        // show the result
        if (result.isNotEmpty())
            homeFragment.contactAdapter.changeList(result)
        else
            Toast.makeText(this@MainActivity, "No result found", Toast.LENGTH_SHORT).show()

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun add_fields()
    {
        val vertical_list = findViewById<LinearLayout>(R.id.fields_vertical_list_filter)
        val edit_text = EditText(this)

        ask_name(edit_text)
        vertical_list.addView(edit_text)
        dynamic_fields.add(edit_text)
    }

    private fun ask_name( edit_text: EditText)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Field Name")
        val dialogLayout = layoutInflater.inflate(R.layout.activity_pop_up, null)
        val input_field_name  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _, _ -> edit_text.hint = input_field_name.text }
        builder.show()
    }

}