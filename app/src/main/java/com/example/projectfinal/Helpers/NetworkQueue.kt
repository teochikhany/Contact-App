package com.example.projectfinal.Helpers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.R
import org.json.JSONArray
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import java.io.File
import com.loopj.android.http.AsyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets


object NetworkQueue
{
    private val client = AsyncHttpClient()
    private const val dbs_url = "http://10.0.2.2:5000/api/dbs"


    fun getAllBakups(users_id: Int, view: View, fct: (id: Int, date:String) -> Unit,context: Context)
    {

        val params = RequestParams()
        params.put("user_id", users_id)

        client.get("$dbs_url/list", params, object : AsyncHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?)
            {
                val response = String(responseBody!!, StandardCharsets.UTF_8)

                val all_backups = JSONArray(response)

                val layout = view.findViewById<LinearLayout>(R.id.backup_LinearLayout)

                for (i in 0 until all_backups.length())
                {
                    val json_object = all_backups.getJSONObject(i)
                    val id_backup = json_object.getInt("id")
                    val date_backup = json_object.getString("date")
                    val name_backup = json_object.getString("name")

                    val text = TextView(view.context)
                    text.textSize = 20f
                    text.text = "Name: $name_backup,\nDate: $date_backup"
                    text.setTextColor(view.resources.getColor(R.color.black))

                    val card = CardView(view.context)
                    card.addView(text)
                    card.radius = 20F
                    card.useCompatPadding = true

                    card.setOnClickListener { fct(id_backup, date_backup) }

                    layout.addView(card)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?)
            {
                Toast.makeText(context, "Cannot connect to Server", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun postBackUp(users_id: Int, db_file: File, name: String, view: View, fct: (id: Int, date:String) -> Unit)
    {
        val params = RequestParams()
        params.put("db_file", db_file)
        params.put("user_id", users_id)
        params.put("name", name)

        client.post(dbs_url, params, object : AsyncHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?)
            {
                Toast.makeText(view.context, "BackUp successful", Toast.LENGTH_SHORT).show()
                val response = String(responseBody!!, StandardCharsets.UTF_8)

                val json = JSONObject(response)

                val id_backup = json.getInt("id")
                val date_backup = json.getString("date")
                val name_backup = json.getString("name")

                val layout = view.findViewById<LinearLayout>(R.id.backup_LinearLayout)
                val text = TextView(view.context)
                text.textSize = 20f
                text.text = "Name: $name_backup,\nDate: $date_backup"
                text.setTextColor(view.resources.getColor(R.color.black))

                val card = CardView(view.context)
                card.addView(text)
                card.radius = 20F
                card.useCompatPadding = true

                card.setOnClickListener { fct(id_backup, date_backup) }

                layout.addView(card, 0)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?)
            {
                Toast.makeText(view.context, "BackUp Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getBackup(db_id: Int, user_id: Int, view: View)
    {
        val params = RequestParams()
        params.put("db_id", db_id)
        params.put("user_id", user_id)

        client.get(dbs_url, params, object : AsyncHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?)
            {
                Log.d("teo", "success")
                Log.d("teo3", responseBody!!.size.toString())

                Contact_database.destroyInstance()

                val filename = "first_test_13"
                val db_file = view.context.getDatabasePath(filename)

                val stream = FileOutputStream(db_file)
                stream.write(responseBody)
                stream.close()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?)
            {
                Toast.makeText(view.context, "Cannot connect to Server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}