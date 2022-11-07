package com.example.proyectofinalv2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.ActivityAddNoteBinding
import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private var note: Note? = null;
    private val addNoteViewModel: MainViewModel by  viewModels {
        MainViewModelFactory((application as NoteApp).database!!.noteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dueDateWrapper.visibility = View.GONE
        binding.cancelar.setOnClickListener{finish()}
        binding.crear.setOnClickListener{createNote()}
        binding.isTaskSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.dueDateWrapper.visibility = View.VISIBLE
            }else{
                binding.dueDateWrapper.visibility = View.GONE
            }
        }

        note = intent.getSerializableExtra("note") as Note?
        if(note!=null){
            binding.apply {
                titleEditView.setText(note!!.title)
                descriptionEditView.setText(note!!.description)
            }
        }
    }

    private fun createNote() {
        val title = binding.titleEditView.text.toString()
        val description = binding.descriptionEditView.text.toString()
        val isATask = binding.isTaskSwitch.isChecked
        var dueDate: Date?
        if (isATask){
            val dueDateStr = binding.dueDate.text.toString()
            val dueDateStrAux = dueDateStr.substring(6)+"-"+dueDateStr.substring(0, 2)+"-"+dueDateStr.substring(3, 5)
            val dueDateAux = LocalDate.parse(dueDateStrAux)
            Toast.makeText(this,dueDateStrAux, Toast.LENGTH_LONG)
            dueDate = localDateToDate(dueDateAux)!!
        }else dueDate = null

        // TODO: Add reminders and media
        if(note==null){
            val newNote = Note(title = title, description = description, isTask = isATask,
                dateCreation = localDateToDate(LocalDate.now()), dueDate = dueDate, isComplete = false, dateCompleted = null)
            addNoteViewModel.insertNewNote(newNote)
        }else{
            var updatedNote = note!!
            updatedNote.title = title
            updatedNote.description = description
            updatedNote.isTask = isATask
            updatedNote.dueDate = dueDate
            addNoteViewModel.updateNote(updatedNote)
        }
        finish()
    }

    fun localDateToDate(localDate: LocalDate): Date? {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }
}