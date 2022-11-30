package com.example.proyectofinalv2.ui.main

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.AlarmReceiver
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.MainViewModelFactory
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.ActivityAddNoteBinding
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.model.Reminder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    val media = mutableListOf<Multimedia>();
    val reminders = mutableListOf<Reminder>();
    val calendars = mutableListOf<Calendar>();
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_VIDEO_CAPTURE = 2
    lateinit var mediaController: MediaController
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    lateinit var currentPhotoPath: String
    lateinit var currentVideoPath: String
    lateinit var photoURI: Uri
    private lateinit var alarmMgr: AlarmManager
    private lateinit var alarmIntent: PendingIntent
    private lateinit var binding: ActivityAddNoteBinding
    private var note: Note? = null;
    private var strDate = ""
    private val addNoteViewModel: MainViewModel by  viewModels {
        MainViewModelFactory((application as NoteApp).database!!.noteDao(),
            (application as NoteApp).database!!.mediaDao(), (application as NoteApp).database!!.reminderDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dueDateWrapper.visibility = View.GONE
        binding.cancelar.setOnClickListener{finish()}
        binding.crear.setOnClickListener{createNote()}
        binding.fotoBtn.setOnClickListener{addPhoto()}
        binding.videoBtn.setOnClickListener{addVideo()}
        binding.reminderBtn.setOnClickListener{addReminder()}
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

        mediaController = MediaController(this)
        mediaController.setAnchorView(
            binding.root);
    }

    private fun addVideo() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                val videoFile: File? = try {
                    createFile()
                } catch (ex: IOException) {
                    null
                }
                videoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.proyectofinalv2.fileprovider",
                        it
                    )
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                }
            }
        }
    }


    private fun addReminder() {
        val getDate = Calendar.getInstance()
        val datepicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = i
                calendar[Calendar.MONTH] = i2
                calendar[Calendar.DAY_OF_MONTH] = i3
                strDate = strDateAux
                showTimePicker()
            },getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
        datepicker.show()

    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .build()
        picker.show(supportFragmentManager, "NOTES_S19120121")
        picker.addOnPositiveButtonClickListener{
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            var strI2 =  picker.hour.toString()
            if(strI2.length==1)strI2 = "0"+picker.hour.toString()
            var strI3 =  picker.minute.toString()
            if(strI3.length==1)strI3 = "0"+picker.minute.toString()
            strDate += "T"+strI2+":"+strI3+":00"//"2007-12-03T10:15:30"
            addReminderText(strDate)
            calendars.add(calendar)
            reminders.add(Reminder(noteId = -1, date = localDateToDate(LocalDateTime.parse(strDate))))
        }
    }
    private fun setUpAlarm() {

        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(applicationContext, 1001, intent, PendingIntent.FLAG_MUTABLE)
        }
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, alarmIntent
            //AlarmManager.ELAPSED_REALTIME_WAKEUP,
            //SystemClock.elapsedRealtime() + 5 * 1000, 5 * 1000,
            //alarmIntent
        )
    }

    private fun addReminderText(strDate: String) {
        val textView = TextView(this)
        textView.setText(strDate)
        binding.remindersLayout.addView(textView);
    }

    fun localDateToDate(localDate: LocalDateTime): Date? {
        return java.util.Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant())
    }

    @Throws(IOException::class)
    fun createFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = filesDir
        return File.createTempFile(
            "PHOTO_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
            currentVideoPath = absolutePath
        }
    }

    private fun addPhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Crear archivo al que ira
                val file: File? = try {
                    createFile()
                } catch (ex: IOException) {
                    null
                }

                // Si se creo correctamente
                file?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.proyectofinalv2.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
             }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageView = ImageView(this)
            imageView.layoutParams = LinearLayout.LayoutParams(400, 400)
            Glide.with(this)
                .load(photoURI)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
            binding.layoutPhotos.addView(imageView);
            media.add(Multimedia(noteId = -1, type = REQUEST_IMAGE_CAPTURE.toLong(), path = photoURI.toString()));
        }else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            val videoView = VideoView(this)
            videoView.layoutParams = LinearLayout.LayoutParams(400, 400)
            videoView.setVideoURI(photoURI)
            videoView.start()
            val mediacontrolleralone = MediaController(this)
            mediacontrolleralone.setAnchorView(videoView)
            videoView.setMediaController(mediacontrolleralone)
            binding.videosLayout.addView(videoView);
            media.add(Multimedia(noteId = -1, type = REQUEST_VIDEO_CAPTURE.toLong(), path = photoURI.toString()));
        }
    }



    private fun createNote() {
        val title = binding.titleEditView.text.toString()
        val description = binding.descriptionEditView.text.toString()
        val isATask = binding.isTaskSwitch.isChecked
        var dueDate: Date?
        if (isATask){
            val dueDateStr = binding.dueDate.text.toString()
            val dueDateStrAux = dueDateStr.substring(6)+"-"+dueDateStr.substring(0, 2)+"-"+dueDateStr.substring(3, 5)+
                    "T08:00:00"
            val dueDateAux = LocalDateTime.parse(dueDateStrAux)
            Toast.makeText(this,dueDateStrAux, Toast.LENGTH_LONG)
            dueDate = localDateToDate(dueDateAux)!!
        }else dueDate = null

        if(note==null){
            val newNote = Note(title = title, description = description, isTask = isATask,
                dateCreation = localDateToDate(LocalDateTime.now()), dueDate = dueDate, isComplete = false, dateCompleted = null)
            addNoteViewModel.insertNewNote(newNote, media, reminders)
            //setUpAlarm()
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




}