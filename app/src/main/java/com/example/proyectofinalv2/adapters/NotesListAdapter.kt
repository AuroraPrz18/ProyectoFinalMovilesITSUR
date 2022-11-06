package com.example.proyectofinalv2.adapters

import android.content.res.Resources
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.domain.model.Note

class NotesListAdapter  (private val notesList: Array<Note>, val onClick: (Note)-> Unit) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>(){
    class ViewHolder (view: View, onClick: (Note)-> Unit): RecyclerView.ViewHolder(view) {
        val titleTextView : TextView
        val completeImageView: ImageView
        val descriptionTextView : TextView
        val mediaImageView: ImageView
        val dateCreatedTextView : TextView
        val dueDateTextView : TextView
        val editButton: Button
        val deleteButton: Button
        var currentNote: Note?= null

        init {
            view.setOnClickListener{
                currentNote?.let {
                    onClick(it)
                }
            }
            titleTextView = view.findViewById(R.id.titleTextView)
            completeImageView = view.findViewById(R.id.completeImageView)
            descriptionTextView = view.findViewById(R.id.descriptionTextView)
            mediaImageView = view.findViewById(R.id.mediaImageView)
            dateCreatedTextView = view.findViewById(R.id.dateCreatedTextView)
            dueDateTextView = view.findViewById(R.id.dueDateTextView)
            editButton = view.findViewById(R.id.editButton)
            deleteButton = view.findViewById(R.id.deleteButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_element, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val note: Note = notesList[position]
        holder.currentNote = note
        holder.titleTextView.text = note.title
        //holder.completeImageView.setImageResource(note.)
        holder.descriptionTextView.text = note.description
        holder.dateCreatedTextView.text = note.dateCreation.toString()
        //holder.dueDateTextView.text = note.dateSetter.toString()
        //holder.editButton.text = // TODO: Write onclick
        //holder.deleteButton.text = // TODO: Write onclick
    }

    override fun getItemCount(): Int {
        return notesList.size
    }
}