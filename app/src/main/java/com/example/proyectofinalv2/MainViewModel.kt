package com.example.proyectofinalv2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.data.NoteDB
import com.example.proyectofinalv2.domain.model.Note
import dagger.hilt.android.internal.Contexts.getApplication

class MainViewModel(app: Application) : AndroidViewModel(app) {
    lateinit var allNotes : MutableLiveData<List<Note>>
    init{
        allNotes = MutableLiveData()
        getAllNotesAndTasks()
    }
    fun getAllNotesObservers(): MutableLiveData<List<Note>> {
        return allNotes
    }
    fun getAllNotesAndTasks(){
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        val notes = noteDao?.getNotes()
        allNotes.value = notes
        //allNotes.postValue(notes)
    }

    fun deleteNote(note: Note){
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        noteDao?.deleteNote(note)
        getAllNotesAndTasks()
    }
    fun insertNewNote(newNote: Note){
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        noteDao?.insertNote(newNote)
        getAllNotesAndTasks()
    }
    fun findNote(note: Note): Note? {
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        return noteDao?.getNoteById(note.id)
    }
    fun updateNewNote(updatedNote: Note){
        val noteDao = NoteDB.getAppDatabase((getApplication()))?.noteDao()
        noteDao?.updateNote(updatedNote)
        getAllNotesAndTasks()
    }
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}