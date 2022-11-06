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
import com.example.proyectofinalv2.databinding.FragmentNotesOnlyBinding
import com.example.proyectofinalv2.databinding.FragmentTasksOnlyBinding
import com.example.proyectofinalv2.domain.model.Note
import java.util.*

class TasksOnlyFragment : Fragment() {
    private var _binding: FragmentTasksOnlyBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = TasksOnlyFragment()
    }

    private lateinit var viewModel: TasksOnlyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksOnlyBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TasksOnlyViewModel::class.java)
        val recyclerView = binding.allTasksOnlyRV

    }

}