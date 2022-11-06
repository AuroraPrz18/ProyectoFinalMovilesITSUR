package com.example.proyectofinalv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.domain.model.Note

class NotesListAdapter (val onClickListeners: ViewHolder.CardViewClickListener) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>(){
    var notesList = ArrayList<Note>()
    fun setData(data: ArrayList<Note>){
        this.notesList = data
    }
    class ViewHolder(view: View, val onClick: CardViewClickListener): RecyclerView.ViewHolder(view) {
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
            titleTextView = view.findViewById(R.id.titleTextView)
            completeImageView = view.findViewById(R.id.completeImageView)
            descriptionTextView = view.findViewById(R.id.descriptionTextView)
            mediaImageView = view.findViewById(R.id.mediaImageView)
            dateCreatedTextView = view.findViewById(R.id.dateCreatedTextView)
            dueDateTextView = view.findViewById(R.id.dueDateTextView)
            editButton = view.findViewById(R.id.editButton)
            deleteButton = view.findViewById(R.id.deleteButton)
        }

        interface CardViewClickListener{
            fun onShowClickListener(note: Note)
            fun onDeleteClickListener(note: Note)
            fun onEditClickListener(note: Note)
            fun onCompleteClickListener(note: Note)
            fun onPostponeClickListener(note: Note)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_element, parent, false)
        return ViewHolder(view, onClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val note: Note = notesList[position]
        holder.apply {
            currentNote = note
            titleTextView.text = note.title
            //completeImageView.setImageResource(note.)
            descriptionTextView.text = note.description
            dateCreatedTextView.text = note.dateCreation.toString()
            //dueDateTextView.text = note.dateSetter.toString()
            //editButton.text = // TODO: Write onclick
            deleteButton.setOnClickListener {
                onClickListeners.onDeleteClickListener(note)
            }
            itemView.setOnClickListener{
                onClickListeners.onShowClickListener(note)
            }
        }


    }

    override fun getItemCount(): Int {
        return notesList.size
    }
}