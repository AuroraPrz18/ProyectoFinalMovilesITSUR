package com.example.proyectofinalv2.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.databinding.FragmentMainBinding
import com.example.proyectofinalv2.databinding.FragmentNotesOnlyBinding
import com.example.proyectofinalv2.domain.model.Note
import java.util.*

class NotesOnlyFragment : Fragment(), NotesListAdapter.ViewHolder.CardViewClickListener {
    private lateinit var binding: FragmentNotesOnlyBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_notes_only, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotesOnlyBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.allNotesOnlyRV
        val adapterV = NotesListAdapter(this@NotesOnlyFragment)
        recyclerView?.apply {
            layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = adapterV
        }


        viewModel.onlyNotes().observe(viewLifecycleOwner){
                list ->
            adapterV.setData(list as ArrayList<Note>)
            adapterV.notifyDataSetChanged()
        }

    }

    override fun onShowClickListener(note: Note) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClickListener(note: Note) {
        viewModel.deleteNote(note)
    }

    override fun onEditClickListener(note: Note) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

}