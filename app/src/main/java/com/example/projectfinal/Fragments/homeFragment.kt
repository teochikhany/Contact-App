package com.example.projectfinal.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfinal.Activities.Contact_View
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.Helpers.MyAdapter
import com.example.projectfinal.R
import com.example.projectfinal.Helpers.SpacingAdapter
import com.example.projectfinal.Helpers.SwipeGesture
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class homeFragment : Fragment() , MyAdapter.OnItemClicked
{
    companion object
    {
        @JvmStatic
        lateinit var contactAdapter: MyAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initRecycleView(view)
        addDataSet(view)

        val search = view.findViewById<SearchView>(R.id.contact_search)

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String): Boolean
            {

                runBlocking {
                    val contact_dao = Contact_database.getDB(view.context).contactDao()
                    val result = contact_dao.getFullName_filter(query)

                    if (result.isNotEmpty())
                        contactAdapter.changeList(result)
                    else
                        Toast.makeText(view.context, "No result found", Toast.LENGTH_SHORT).show()
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean
            {
                return false
            }
        })


        search.setOnCloseListener {

            runBlocking {
                val contactDao = Contact_database.getDB(view.context).contactDao()
                contactAdapter.changeList(contactDao.getFullName())
            }

            false
        }

        return view
    }


    private fun initRecycleView(view: View)
    {
        val recycleView = view.findViewById<RecyclerView>(R.id.contact_recycler_view)
        recycleView.layoutManager = LinearLayoutManager(this@homeFragment.context)
        val padding = SpacingAdapter(30)
        recycleView.addItemDecoration(padding)
        contactAdapter = MyAdapter()

        val swipeGesture = object : SwipeGesture()
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                when (direction)
                {
                    ItemTouchHelper.LEFT ->
                    {
                        runBlocking {
                            val contactId = contactAdapter.getItemPosition(viewHolder.adapterPosition).contact_id
                            val fullcontact = Contact_database.getDB(view.context).contactDao().getFulContact(contactId)
                            val position_old = viewHolder.adapterPosition

                            contactAdapter.deleteItem(viewHolder.adapterPosition, view.context)

                            val snack = Snackbar.make(view, "Contact Deleted", Snackbar.LENGTH_LONG)
                            snack.duration = 7000
                            snack.setAction("UNDO") { contactAdapter.AddItem(position_old, view.context, fullcontact) }
                            snack.show()
                        }
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recycleView)

        recycleView.adapter = contactAdapter

        contactAdapter.setOnClick(this@homeFragment)
    }


    private fun addDataSet(view: View)
    = runBlocking {
        launch {
            val contactDao = Contact_database.getDB(view.context).contactDao()
            contactAdapter.submitList(contactDao.getFullName())
        }
    }

    override fun onItemClick(position: Int)
    {
        val intent = Intent(this@homeFragment.context, Contact_View::class.java)
        intent.putExtra("contactID", contactAdapter.getItemPosition(position).contact_id.toString())
        intent.putExtra("position", position)
        startActivity(intent)
    }

}