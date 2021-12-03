package com.example.projectfinal.Helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfinal.Databases.Contact_database
import com.example.projectfinal.Model.Contact_Info
import com.example.projectfinal.Model.NameTuple
import com.example.projectfinal.R
import kotlinx.coroutines.runBlocking

class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var contact_list: MutableList<NameTuple> = mutableListOf()

    private var onClick: OnItemClicked? = null


    fun deleteItem(position: Int, context: Context)
    {

        runBlocking{
            Contact_database.getDB(context).contactDao().DeleteContact(contact_list[position].contact_id)
        }

        contact_list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun AddItem(position: Int, context: Context, contact: Contact_Info)
    {
        runBlocking{
            val contactDAO = Contact_database.getDB(context).contactDao()
            val newID = contactDAO.insert(contact)

            contact_list.add(position, contactDAO.getContact(newID.toInt()) )
            notifyItemInserted(position)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return ViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_single_contact_element, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (holder)
        {
            is ViewHolder ->
            {
                holder.bind(contact_list[position])
            }
        }


        val element = holder.itemView.findViewById<CardView>(R.id.single_element_card)
        element.setOnClickListener { onClick!!.onItemClick(position) }
    }

    override fun getItemCount(): Int
    {
        return contact_list.size
    }

    fun submitList(dicelist: List<NameTuple>)
    {
        contact_list = dicelist.toMutableList()
    }


    fun ItemChange(new_name: NameTuple,position:Int)
    {
        contact_list[position] = new_name
        notifyItemChanged(position)
    }

    fun changeList(dicelist: List<NameTuple>)
    {
        contact_list = dicelist.toMutableList()
        notifyDataSetChanged()
    }

    fun getItemPosition(position: Int) : NameTuple
    {
        return contact_list[position]
    }


    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val textView = itemView.findViewById<TextView>(R.id.contact_name)

        fun bind(first_last_name: NameTuple)
        {
            val temp =  first_last_name.firstName + " " + first_last_name.lastName
            textView.text = temp
        }
    }

    interface OnItemClicked
    {
        fun onItemClick(position: Int)
    }

    fun setOnClick(onClick: OnItemClicked?)
    {
        this.onClick = onClick
    }

}

// This is for adding spaces between the cards
class SpacingAdapter(private val padding: Int) : RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = padding
        outRect.left = padding
        outRect.right = padding
    }
}

// this is to add the swipe gesture and background Color
abstract class SwipeGesture : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
{
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
    {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean)
    {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val background = ColorDrawable(Color.RED)

        if (dX < 0) // Swiping to the left
        {
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }
        else // view is unSwiped
        {
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
    }
}