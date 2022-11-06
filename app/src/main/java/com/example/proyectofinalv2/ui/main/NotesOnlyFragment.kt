package com.example.proyectofinalv2.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.databinding.FragmentMainBinding
import com.example.proyectofinalv2.databinding.FragmentNotesOnlyBinding
import com.example.proyectofinalv2.domain.model.Note
import java.util.*

class NotesOnlyFragment : Fragment() {
    private var _binding: FragmentNotesOnlyBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = NotesOnlyFragment()
    }

    private lateinit var viewModel: NotesOnlyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesOnlyBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotesOnlyViewModel::class.java)
        val recyclerView = binding.allNotesOnlyRV
        recyclerView?.layoutManager =
            LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        // TODO: Get list from model view
        val Note1 = Note(title = "Nota1tasks", description = "Desc1", isTask = true, dateCreation = Date(12, 12, 2022), dueDate = Date(12, 12, 2022), isComplete = false, dateCompleted = null)
        val Note2 = Note(title = "Nota2tasks", description = "Desc2", isTask = true, dateCreation = Date(11, 11, 2021), dueDate = Date(12, 12, 2022), isComplete = false, dateCompleted = null)
        val notes : Array<Note> = arrayOf<Note>(Note1, Note2)
        recyclerView?.adapter = NotesListAdapter(notes, {
            Toast.makeText(activity
                , "Escogio ${it.title}",
                Toast.LENGTH_SHORT).show()
        })
    }

}