package com.example.proyectofinalv2

import android.app.Application
import androidx.lifecycle.*
import com.example.proyectofinalv2.data.dao.MediaDAO
import com.example.proyectofinalv2.data.dao.NoteDao
import com.example.proyectofinalv2.data.dao.ReminderDAO
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.model.Reminder
import com.example.proyectofinalv2.domain.repository.MediaRepositoryClass
import com.example.proyectofinalv2.domain.repository.NoteRepositoryClass
import com.example.proyectofinalv2.domain.repository.ReminderRepositoryClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val noteDao: NoteDao, private val mediaDao: MediaDAO, private val reminderDao: ReminderDAO) : ViewModel() {
    private var repository: NoteRepositoryClass = NoteRepositoryClass(noteDao)
    private var repositoryMult: MediaRepositoryClass = MediaRepositoryClass(mediaDao)
    private var repositoryRemi: ReminderRepositoryClass = ReminderRepositoryClass(reminderDao)
    val notesLiveData = MutableLiveData<List<Note>>()

    fun allReminders(): LiveData<List<Reminder>> = repositoryRemi.allReminders()
    fun allMedia(): LiveData<List<Multimedia>> = repositoryMult.allMedias()
    fun allNotes(): LiveData<List<Note>> = repository.allNotes()
    fun onlyTasks(): LiveData<List<Note>> = repository.onlyTasks()
    fun onlyNotes(): LiveData<List<Note>> = repository.onlyNotes()
    //fun search(searchStr: String): List<Note> =  repository.search(searchStr)
    fun deleteNote(note: Note)= viewModelScope.launch(Dispatchers.IO){
        repository.deleteNote(note)
    }
    fun insertNewNote(note: Note, multimedias: List<Multimedia>, reminders: List<Reminder>) = viewModelScope.launch(Dispatchers.IO){
        val noteId = repository.insertNote(note)
        for (multimedia in multimedias) {
            multimedia.noteId=noteId
            repositoryMult.insertMultimedia(multimedia)
        }
        for (reminder in reminders) {
            reminder.noteId=noteId
            repositoryRemi.insertReminder(reminder)
            // todo: Agregar alarma
        }
    }
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.updateNote(note)
    }

}

class MainViewModelFactory(private val noteDao: NoteDao, private val mediaDao: MediaDAO, private val reminderDao: ReminderDAO) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(noteDao, mediaDao, reminderDao) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}