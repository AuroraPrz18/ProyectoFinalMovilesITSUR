package com.example.proyectofinalv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Reminder

class RemindersListAdapter (val onClickListeners: RemindersListAdapter.ViewHolder.CardViewClickListener) : RecyclerView.Adapter<RemindersListAdapter.ViewHolder>(){
    var remindersList = ArrayList<Reminder>()
    fun setData(reminders:ArrayList<Reminder>){
        this.remindersList = reminders
    }
    class ViewHolder (view: View, val onClick: RemindersListAdapter.ViewHolder.CardViewClickListener): RecyclerView.ViewHolder(view){
        val infoTxt: TextView
        val deleteBtn: ImageView
        init{
            infoTxt = view.findViewById(R.id.infoTxt)
            deleteBtn = view.findViewById(R.id.deleteBtn)
        }
        interface CardViewClickListener{
            fun onDeleteClickListener(reminder: Reminder)
            fun onEditClickListener(reminder: Reminder)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemindersListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_element, parent, false)
        return ViewHolder(view, onClickListeners)
    }

    override fun onBindViewHolder(holder: RemindersListAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
        val reminder: Reminder = remindersList[position]
        holder.apply {
            infoTxt.text = reminder.date.toString()
            deleteBtn.setOnClickListener {
                onClickListeners.onDeleteClickListener(reminder)
            }
        }
    }

    override fun getItemCount(): Int {
        return remindersList.size
    }

}