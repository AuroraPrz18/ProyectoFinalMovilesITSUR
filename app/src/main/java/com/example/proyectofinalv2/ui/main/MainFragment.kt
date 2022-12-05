package com.example.proyectofinalv2.ui.main

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
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
import com.example.proyectofinalv2.domain.model.Reminder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class MainFragment : Fragment(), NotesListAdapter.ViewHolder.CardViewClickListener {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapterV: NotesListAdapter
    private var mediasList = ArrayList<Multimedia>()
    private var notesList = ArrayList<Note>()
    private var remindersList = ArrayList<Reminder>()
    private lateinit var picker: MaterialTimePicker

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
        viewModel.allReminders().observe(viewLifecycleOwner){
                list ->
            remindersList = list as ArrayList<Reminder>
            setUpAlarm()
        }
        viewModel.allNotes().observe(viewLifecycleOwner){
            list ->
            notesList = list as ArrayList<Note>
            adapterV.setData(notesList, mediasList, remindersList)
            adapterV.notifyDataSetChanged()
        }

        binding.mainSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null && query!!.length>0){
                    var list = ArrayList<Note>()
                    for(note in notesList){
                        if(note.title!=null && note.title!!.contains(query)){
                            list.add(note)
                        }else if(note.description!=null && note.description!!.contains(query)){
                            list.add(note)
                        }
                    }
                    adapterV.setData(list, mediasList, remindersList)
                    adapterV.notifyDataSetChanged()
                }else{
                    adapterV.setData(notesList, mediasList, remindersList)
                    adapterV.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null && newText!!.length>0){
                }else{
                    adapterV.setData(notesList, mediasList, remindersList)
                    adapterV.notifyDataSetChanged()
                }
                return false
            }
            })
    }
    fun isTablet(): Boolean {
        val xlarge = getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
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
        Toast.makeText(activity, "sss", Toast.LENGTH_LONG).show()
        val getDate = Calendar.getInstance()
        var calendar: Calendar = Calendar.getInstance()
        val datepicker = DatePickerDialog(this.context!!, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            DatePickerDialog.OnDateSetListener{ datePicker, i, i2, i3 ->
                val selectDate: Calendar = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
                var strI2 =  (i2+1).toString()
                if(strI2.length==1)strI2 = "0"+(i2+1).toString()
                var strI3 =  i3.toString()
                if(strI3.length==1)strI3 = "0"+i3.toString()
                val strDateAux = (i.toString()+"-"+strI2+"-"+strI3)
                var strDate = strDateAux
                calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = i
                calendar[Calendar.MONTH] = i2
                calendar[Calendar.DAY_OF_MONTH] = i3
                showTimePicker(calendar, strDate, note)
            },getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
        datepicker.show()




    }
    private fun showTimePicker(calendar: Calendar, strDate:String, note:Note) {
        var strDateA = strDate
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .build()
        picker.show(activity!!.supportFragmentManager, "NOTES_S19120121")
        picker.addOnPositiveButtonClickListener{
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            var strI2 =  picker.hour.toString()
            if(strI2.length==1)strI2 = "0"+picker.hour.toString()
            var strI3 =  picker.minute.toString()
            if(strI3.length==1)strI3 = "0"+picker.minute.toString()
            strDateA += "T"+strI2+":"+strI3+":00"//"2007-12-03T10:15:30"
            viewModel.insertReminder(Reminder(noteId = note.id, date = localDateToDate(LocalDateTime.parse(strDateA)), isSetUp = false, isDueDate = true))
            deleteReminders(note)
            var noteAux = note;
            noteAux.dueDate = localDateToDate(LocalDateTime.parse(strDateA))
            viewModel.updateNote(noteAux)
            adapterV.notifyDataSetChanged()
        }
    }

    fun setUpAlarm() {
        for(reminder in remindersList){
            if(reminder.isSetUp == false){
                val intent = Intent(activity!!.applicationContext, AlarmReceiver::class.java)
                var titleD = getString(R.string.notificationTitleForTask)
                var descD = getString(R.string.notificationDescForTask)
                for(note in notesList){
                    if(note.id == reminder.noteId){
                        titleD = note.title!!
                        if(reminder.isDueDate==true){
                            titleD = getString(R.string.notificationTitleForDueDate)
                        }
                        intent.putExtra("note", note)
                    }
                }
                intent.apply {
                    putExtra("title", titleD)
                    putExtra("desc", descD)
                    putExtra("notiId", reminder.id.toInt())
                }

                var pendingIntent = PendingIntent.getBroadcast(
                    activity!!.applicationContext,
                    reminder.id.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                var calendarA = Calendar.getInstance()
                calendarA.time =reminder.date
                val alarmMgr = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmMgr.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendarA.timeInMillis,
                    pendingIntent
                )
                reminder.isSetUp = true
                viewModel.updateReminder(reminder)
            }
        }
    }

    fun deleteReminders(note: Note){
        for(reminderV in remindersList){
            if(reminderV.noteId==note.id && reminderV.isDueDate==true){
                val intent = Intent(activity!!.applicationContext, AlarmReceiver::class.java)
                var titleD = getString(R.string.notificationTitleForTask)
                var descD = getString(R.string.notificationDescForTask)
                for(note in notesList){
                    if(note.id == reminderV.noteId){
                        titleD = note.title!!
                        if(reminderV.isDueDate==true){
                            titleD = getString(R.string.notificationTitleForDueDate)
                        }
                        intent.putExtra("note", note)
                    }
                }
                intent.apply {
                    putExtra("title", titleD)
                    putExtra("desc", descD)
                    putExtra("notiId", reminderV.id.toInt())
                }
                var pendingIntent = PendingIntent.getBroadcast(
                    activity!!.applicationContext,
                    reminderV.id.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                var alarmMgr = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmMgr.cancel(pendingIntent)
                viewModel.deleteReminder(reminderV)
            }
        }
    }

    fun localDateToDate(localDate: LocalDateTime): Date? {
        return java.util.Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant())
    }


}
