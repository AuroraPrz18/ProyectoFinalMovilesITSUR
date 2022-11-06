package com.example.proyectofinalv2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.data.NoteDB
import com.example.proyectofinalv2.domain.model.Note

class AddNoteViewModel (app: Application) : AndroidViewModel(app) {
    lateinit var allNotes : MutableLiveData<List<Note>>
    init{
        allNotes = MutableLiveData()
    }
    fun getAllNotesObservers(): MutableLiveData<List<Note>> {
        return allNotes
    }
    fun insertNewNote(newNote: Note){
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        noteDao?.insertNote(newNote)
        getAllNotesObservers()
    }
    fun updateNewNote(updatedNote: Note){
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        noteDao?.updateNote(updatedNote)
        getAllNotesObservers()
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddNoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddNoteViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}