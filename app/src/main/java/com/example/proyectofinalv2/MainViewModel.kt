package com.example.proyectofinalv2

import android.app.Application
import androidx.lifecycle.*
import com.example.proyectofinalv2.data.dao.NoteDao
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.repository.NoteRepositoryClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val noteDao: NoteDao) : ViewModel() {
    private var repository: NoteRepositoryClass = NoteRepositoryClass(noteDao)
    val notesLiveData = MutableLiveData<List<Note>>()

    fun allNotes(): LiveData<List<Note>> = repository.allNotes()
    fun onlyTasks(): LiveData<List<Note>> = repository.onlyTasks()
    fun onlyNotes(): LiveData<List<Note>> = repository.onlyNotes()

    fun deleteNote(note: Note)= viewModelScope.launch(Dispatchers.IO){
        repository.deleteNote(note)
    }
    fun insertNewNote(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.insertNote(note)
    }
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.updateNote(note)
    }

}

class MainViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}