package com.example.proyectofinalv2.ui.main

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalv2.*
import com.example.proyectofinalv2.adapters.NotesListAdapter
import com.example.proyectofinalv2.databinding.FragmentMainBinding
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class MainFragment : Fragment(), NotesListAdapter.ViewHolder.CardViewClickListener {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapterV: NotesListAdapter
    private var mediasList = ArrayList<Multimedia>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.allNotesRV
        adapterV = NotesListAdapter(this@MainFragment)
        var layoutManagerRV: RecyclerView.LayoutManager
        if(isTablet() == true){
            layoutManagerRV = GridLayoutManager(activity, 3)
        }else{
            layoutManagerRV =
                    LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        }
        recyclerView?.apply {
            layoutManager = layoutManagerRV
            adapter = adapterV
        }

        viewModel.allMedia().observe(viewLifecycleOwner){
                list ->
            mediasList = list as ArrayList<Multimedia>
        }
        viewModel.allNotes().observe(viewLifecycleOwner){
            list ->
            adapterV.setData(list as ArrayList<Note>, mediasList)
            adapterV.notifyDataSetChanged()
        }

        /*binding.mainSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null && newText!!.length>0){
                    val list = viewModel.search(newText)
                    adapterV.setData(list as ArrayList<Note>)
                    adapterV.notifyDataSetChanged()
                }else{
                    val list = viewModel.allNotes()
                    adapterV.setData(list as ArrayList<Note>)
                    adapterV.notifyDataSetChanged()
                }
                return false
            }
            })*/

    }
    fun isTablet(): Boolean {
        val xlarge = getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    override fun onShowClickListener(note: Note) {
        TODO("Not yet implemented")
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
