package com.example.proyectofinalv2.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.MainViewModelFactory
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.FragmentMainBinding
import com.example.proyectofinalv2.domain.model.Note
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MainFragment : Fragment(), NotesListAdapter.ViewHolder.CardViewClickListener {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.allNotesRV
        val adapterV = NotesListAdapter(this@MainFragment)
        recyclerView?.apply {
            layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = adapterV
        }


        viewModel.allNotes().observe(viewLifecycleOwner){
            list ->
            adapterV.setData(list as ArrayList<Note>)
            adapterV.notifyDataSetChanged()
        }

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