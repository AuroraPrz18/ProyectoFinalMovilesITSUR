package com.example.proyectofinalv2.ui.main

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.databinding.FragmentTasksOnlyBinding
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.model.Reminder
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class TasksOnlyFragment : Fragment(), NotesListAdapter.ViewHolder.CardViewClickListener {
    private lateinit var binding: FragmentTasksOnlyBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var mediasList = ArrayList<Multimedia>()
    private var remindersList = ArrayList<Reminder>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tasks_only, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksOnlyBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.allTasksOnlyRV
        val adapterV = NotesListAdapter(this@TasksOnlyFragment)
        recyclerView?.apply {
            layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = adapterV
        }

        viewModel.allMedia().observe(viewLifecycleOwner){
                list ->
            mediasList = list as ArrayList<Multimedia>
        }
        viewModel.allReminders().observe(viewLifecycleOwner){
                list ->
            remindersList = list as ArrayList<Reminder>
        }
        viewModel.onlyTasks().observe(viewLifecycleOwner){
                list ->
            adapterV.setData(list as ArrayList<Note>, mediasList, remindersList)
            adapterV.notifyDataSetChanged()
        }

    }

    override fun onShowClickListener(note: Note) {
        val intent = Intent(activity, DetailsActivity::class.java)
        intent.putExtra("media", mediasList)
        intent.putExtra("reminder", remindersList)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    override fun onDeleteClickListener(note: Note) {
        viewModel.deleteNote(note)
    }

    override fun onEditClickListener(note: Note) {
        val intent = Intent(activity, AddNoteActivity::class.java)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    override fun onCompleteClickListener(note: Note) {
        if(note.isComplete == false){
            var noteAux = note
            noteAux.isComplete = true
            viewModel.updateNote(note)
        }else{
            Toast.makeText(activity, "Tu ya completaste esta tarea", Toast.LENGTH_LONG)
        }
    }

    override fun onPostponeClickListener(note: Note) {
        val getDate = Calendar.getInstance()
        val datepicker = DatePickerDialog(this.context!!, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            DatePickerDialog.OnDateSetListener{ datePicker, i, i2, i3 ->
                val selectDate: Calendar = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
                var noteAux = note
                var strI2 =  (i2+1).toString()
                if(strI2.length==1)strI2 = "0"+(i2+1).toString()
                var strI3 =  i3.toString()
                if(strI3.length==1)strI3 = "0"+i3.toString()
                val strDate = (i.toString()+"-"+strI2+"-"+strI3)
                noteAux.dueDate = localDateToDate(LocalDate.parse(strDate))
                viewModel.updateNote(note)
            },getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
        datepicker.show()
    }
    fun localDateToDate(localDate: LocalDate): Date? {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }
}