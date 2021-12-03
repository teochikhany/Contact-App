package com.example.projectfinal.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.projectfinal.Activities.BackupActivity
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.Helpers.NetworkQueue
import com.example.projectfinal.R


class AccountFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val btn = view.findViewById<Button>(R.id.openBackup)
        btn.setOnClickListener {
            val intent = Intent(view.context, BackupActivity::class.java)
            startActivity(intent)
        }

        return view
    }

 }