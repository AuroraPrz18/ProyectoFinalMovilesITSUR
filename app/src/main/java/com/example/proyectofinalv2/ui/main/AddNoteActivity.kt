package com.example.proyectofinalv2.ui.main

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.*
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.ActivityAddNoteBinding
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.model.Reminder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
class AddNoteActivity : AppCompatActivity() {
    private var notesList = ArrayList<Note>()
    private var remindersList = ArrayList<Reminder>()
    val media = mutableListOf<Multimedia>();
    val reminders = mutableListOf<Reminder>();
    val calendars = mutableListOf<Calendar>();
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_VIDEO_CAPTURE = 2
    private val REQUEST_AUDIO_CAPTURE = 3
    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,
        "android.permission.WRITE_EXTERNAL_STORAGE")
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
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                } else {
                }
            }

        binding.dueDateWrapper.visibility = View.GONE
        binding.cancelar.setOnClickListener{finish()}
        binding.crear.setOnClickListener{createNote()}
        binding.fotoBtn.setOnClickListener{addPhoto()}
        binding.videoBtn.setOnClickListener{addVideo()}
        //binding.audioBtn.setOnClickListener{addAudio()}
        binding.reminderBtn.setOnClickListener{addReminder()}
        binding.isTaskSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.dueDateWrapper.visibility = View.VISIBLE
            }else{
                binding.dueDateWrapper.visibility = View.GONE
            }
        }

        addNoteViewModel.allNotes().observe(this){
                list ->
            notesList = list as ArrayList<Note>
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

    var isRecording = true
    var isPlaying = true
    private fun addAudio() {
        askForPermission()
        onRecord(isRecording)
        isRecording = !isRecording
        val button = Button(this)
        button.layoutParams = LinearLayout.LayoutParams(200, 200)
        button.setText("|>")
        button.setOnClickListener {
            onPlay(isPlaying)
        }
        isPlaying = !isPlaying
        binding.audiosLayout.addView(button);
        media.add(Multimedia(noteId = -1, type = REQUEST_AUDIO_CAPTURE.toLong(), path = fileName));
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
            reminders.add(Reminder(noteId = -1, date = localDateToDate(LocalDateTime.parse(strDate)), isSetUp = false))
        }
    }

    private fun addReminderText(strDate: String) {
        val textView = TextView(this)
        textView.setText(strDate)
        binding.remindersLayout.addView(textView)
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

        addNoteViewModel.allReminders().observe(this){
                list ->
            remindersList = list as ArrayList<Reminder>
            setUpAlarm()
        }

        if(note==null){
            val newNote = Note(title = title, description = description, isTask = isATask,
                dateCreation = localDateToDate(LocalDateTime.now()), dueDate = dueDate, isComplete = false, dateCompleted = null)
            addNoteViewModel.insertNewNote(newNote, media, reminders)
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

    fun setUpAlarm() {
        for(reminder in remindersList){
            if(reminder.isSetUp == false){
                val intent = Intent(applicationContext, AlarmReceiver::class.java)
                var titleD = getString(R.string.notificationTitleForTask)
                var descD = getString(R.string.notificationDescForTask)
                for(note in notesList){
                    if(note.id == reminder.noteId){
                        titleD = note.title!!
                        intent.putExtra("note", note)
                    }
                }
                intent.apply {
                    putExtra("title", titleD)
                    putExtra("desc", descD)
                    putExtra("notiId", reminder.id.toInt())
                }

                var pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    reminder.id.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                var calendarA = Calendar.getInstance()
                calendarA.time =reminder.date
                alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmMgr.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendarA.timeInMillis,
                    pendingIntent
                )
                reminder.isSetUp = true
                addNoteViewModel.updateReminder(reminder)
            }
        }
    }

    // Audiooo
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED
                            )) {
                    startRecordingAudio()
                } else {
                    Toast.makeText(this, "No cuenta con los permisos suficientes", Toast.LENGTH_LONG)
                }
                return
            }
        }
    }

    private fun startRecordingAudio() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            createAudioFile()
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("Audio", "prepare() failed")
            }
            start()
        }
    }

    @Throws(IOException::class)
    fun createAudioFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        return File.createTempFile(
            "AUDIO_${timeStamp}_", /* prefix */
            ".mp3", /* suffix */
            storageDir /* directory */
        ).apply {
            fileName = absolutePath
        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        Toast.makeText(this, "Grabado", Toast.LENGTH_LONG)
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("Audio", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }
    private fun askForPermission() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                "android.permission.RECORD_AUDIO"
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") -> {
                MaterialAlertDialogBuilder(this
                )
                    .setTitle("Title")
                    .setMessage("Debes dar perimso para grabar audios")
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("OK") { dialog, which ->
                        requestPermissions(
                            arrayOf("android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE"),
                            1001)

                    }
                    .show()
            }
            else -> {
                requestPermissions(
                    arrayOf("android.permission.RECORD_AUDIO",
                        "android.permission.WRITE_EXTERNAL_STORAGE"),
                    1001)
            }
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("Audio", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }



    /*override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Record to the external cache directory for visibility
        fileName = "${externalCacheDir.absolutePath}/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        recordButton = RecordButton(this)
        playButton = PlayButton(this)
        val ll = LinearLayout(this).apply {
            addView(recordButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
            addView(playButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f))
        }
        setContentView(ll)
    }*/

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }
}


