package com.example.projectfinal.Activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.Helpers.NetworkQueue
import com.example.projectfinal.R

class BackupActivity : AppCompatActivity()
{
    private val userID = 1

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        NetworkQueue.getAllBakups(userID, view = findViewById(R.id.backupConstraint) , this::onClick,this@BackupActivity)

        val uploadBtn = findViewById<Button>(R.id.UploadBackup)
        uploadBtn.setOnClickListener { upload_database(view = findViewById(R.id.backupConstraint)) }
    }

    private fun upload_database(view: View)
    {

        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Database Name")
        val dialogLayout = layoutInflater.inflate(R.layout.activity_pop_up, null)
        val input_field_name  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)

        builder.setPositiveButton("OK") { _, _ ->
            val name = input_field_name.text.toString()
            Contact_database.destroyInstance()

            val filename = "first_test_13"
            val db_file = view.context.getDatabasePath(filename)

            NetworkQueue.postBackUp(userID, db_file, name, view, this::onClick)
        }

        builder.show()
    }

    private fun onClick(id: Int, date:String)
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.restore)

        val dialogLayout = layoutInflater.inflate(R.layout.restore_backup, null)

        val longitude_delte  = dialogLayout.findViewById<TextView>(R.id.restore_backup_text)

        longitude_delte.text = " " + getString(R.string.restore_backup) + "\n id: " + id + ",\n date: " + date

        builder.setView(dialogLayout)

        builder.setPositiveButton("OK") { _, _ -> NetworkQueue.getBackup(id, userID, view = findViewById(R.id.backupConstraint)) }
        builder.setNegativeButton("Cancel") {_, _ ->}

        builder.show()
    }
}