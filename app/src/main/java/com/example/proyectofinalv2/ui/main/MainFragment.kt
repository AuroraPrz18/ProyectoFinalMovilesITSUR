package com.example.proyectofinalv2.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.FragmentMainBinding
import com.example.proyectofinalv2.domain.model.Note
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment(), NotesListAdapter.ViewHolder.CardViewClickListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: NotesListAdapter

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.allNotesRV
        recyclerView?.apply {
            layoutManager =
                LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            adapter = NotesListAdapter(this@MainFragment)
        }
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        Toast.makeText(activity, viewModel.allNotes.value.toString(), Toast.LENGTH_LONG)
        //NotesListAdapter(this@MainFragment).setData(ArrayList(viewModel.allNotes.value))
        viewModel.allNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            NotesListAdapter(this@MainFragment).setData(ArrayList(it))
            NotesListAdapter(this@MainFragment).notifyDataSetChanged()
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onShowClickListener(note: Note) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClickListener(note: Note) {
        //viewModel.deleteNote(note)
    }

    override fun onEditClickListener(note: Note) {
        TODO("Not yet implemented")
    }

    override fun onCompleteClickListener(note: Note) {
        TODO("Not yet implemented")
    }

    override fun onPostponeClickListener(note: Note) {
        TODO("Not yet implemented")
    }

}