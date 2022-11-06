package com.example.proyectofinalv2.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.databinding.FragmentMainBinding
import com.example.proyectofinalv2.domain.model.Note
import java.util.*

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val recyclerView = binding.allNotesRV
        recyclerView?.layoutManager =
            LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        // TODO: Get list from model view
        val Note1 = Note(title = "Nota1", description = "Desc1", isTask = true, dateCreation = Date(12, 12, 2022), dateSetter = Date(12, 12, 2022), author = "Yo", media="nada")
        val Note2 = Note(title = "Nota2", description = "Desc2", isTask = true, dateCreation = Date(11, 11, 2021), dateSetter = Date(11, 11, 2021), author = "Yo", media="nada")
        val notes : Array<Note> = arrayOf<Note>(Note1, Note2)
        recyclerView?.adapter = NotesListAdapter(notes, {
            Toast.makeText(activity
                , "Escogio ${it.title}",
                Toast.LENGTH_SHORT).show()
        })
    }

}