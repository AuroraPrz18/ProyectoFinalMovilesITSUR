package com.example.proyectofinalv2.ui.main

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.*
import com.example.proyectofinalv2.adapters.MediaListAdapter
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
import kotlin.collections.ArrayList

private const val REQUEST_CODE =200
class AddNoteActivity : AppCompatActivity(), MediaListAdapter.ViewHolder.CardViewClickListener {
    // Arrays LiveData
    private var notesList = ArrayList<Note>()
    private var remindersList = ArrayList<Reminder>()
    private var mediasList = ArrayList<Multimedia>() // En recyclerView
    // Arrays auxiliares
    private val media = mutableListOf<Multimedia>();
    private val reminders = mutableListOf<Reminder>();
    private val calendars = mutableListOf<Calendar>();
    //Permisos
    private var permissionGranted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,
        "android.permission.WRITE_EXTERNAL_STORAGE")
    // Constantes para reconocer multimedia
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_VIDEO_CAPTURE = 2
    private val REQUEST_AUDIO_CAPTURE = 3
    // Paths
    private var audioFileName: String = ""
    lateinit var currentPhotoPath: String
    lateinit var currentVideoPath: String
    lateinit var photoURI: Uri
    // Audio
    private lateinit var recorder: MediaRecorder
    private var player: MediaPlayer? = null
    // Video
    lateinit var mediaController: MediaController
    //Reminder
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmMgr: AlarmManager
    private var strDate = ""
    //Recyvlerview
    private lateinit var adapterM: MediaListAdapter


    private lateinit var binding: ActivityAddNoteBinding
    private var note: Note? = null;
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
        binding.audioBtn.setOnClickListener{addAudio()}
        binding.reminderBtn.setOnClickListener{addReminder()}
        binding.isTaskSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.dueDateWrapper.visibility = View.VISIBLE
            }else{
                binding.dueDateWrapper.visibility = View.GONE
            }
        }

        getMedia()
        addNoteViewModel.allNotes().observe(this){
                list ->
            notesList = list as ArrayList<Note>
        }
        addNoteViewModel.allMedia().observe(this){
                list ->
            mediasList.clear()
            // Add those in the DB
            for(mediaV in list){
                if(note!=null && mediaV.noteId == note!!.id){
                    mediasList.add(mediaV);
                }
            }
            // Add those news
            for(mediaV in media){
                mediasList.add(mediaV);
            }
            adapterM.setData(mediasList)
            adapterM.notifyDataSetChanged()
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

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) ===
                PackageManager.PERMISSION_GRANTED
        ActivityCompat.requestPermissions(this,
            arrayOf("android.permission.RECORD_AUDIO",
                "android.permission.WRITE_EXTERNAL_STORAGE"),
            REQUEST_CODE)
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
    private fun getMedia() {
        val recyclerView = binding.mediaRV
        for(mediaV in mediasList){
            media.add(mediaV)
        }
        adapterM = MediaListAdapter(this@AddNoteActivity)
        var layoutManagerRV: RecyclerView.LayoutManager
        if(isTablet() == true){
            layoutManagerRV = GridLayoutManager(this, 3)
        }else{
            layoutManagerRV =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        recyclerView?.apply {
            layoutManager = layoutManagerRV
            adapter = adapterM
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
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED && //Permiso audio
                            grantResults[1] == PackageManager.PERMISSION_GRANTED  // Permiso escritura
                            )) {
                    permissionGranted = true
                } else {
                    Toast.makeText(this, "No cuenta con los permisos suficientes", Toast.LENGTH_LONG)
                    permissionGranted = false
                }
                return
            }
        }
    }

    var isRecording = false
    var isPlaying = false
    private fun addAudio() {
        if(permissionGranted) {
            when {
                isRecording -> stopRecording()
                else -> startRecording()
            }
        }else{
            Toast.makeText(this, "There are not enough permissions to use the audio recorder",
            Toast.LENGTH_LONG).show()
        }

    }

    private fun stopRecording() {
        recorder.apply{
            stop()
            release()
        }
        isRecording = false
        binding.audioBtn.setMaxImageSize(100)
        val button = ImageView(this)
        button.layoutParams = LinearLayout.LayoutParams(200, 200)
        button.setImageResource(R.drawable.ic_play)
        button.setOnClickListener {
            if(!isPlaying) button.setImageResource(R.drawable.ic_playing)
            else button.setImageResource(R.drawable.ic_play)
            onPlay(isPlaying)
            isPlaying = !isPlaying
        }
        binding.audiosLayout.addView(button);
        media.add(Multimedia(noteId = -1, type = REQUEST_AUDIO_CAPTURE.toLong(), path = audioFileName));
    }

    private fun onPlay(start: Boolean) = if (start) {
        stopPlaying()
    } else {
        startPlaying()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(audioFileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("Audio error", "prepare() failed")
            }
        }
    }

    private fun startRecording() {
        // Start recording
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            createAudioFile()
            setOutputFile(audioFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            }catch (e: IOException){
                Log.e("Audio error", "Algo ha fallado")
            }
            isRecording = true
            start()
            binding.audioBtn.setMaxImageSize(40)
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
            audioFileName = absolutePath
        }
    }

    // Recycler view
    fun isTablet(): Boolean {
        val xlarge = getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    override fun onDeleteClickListener(mediaV: Multimedia) {
        adapterM.setData(mediasList)
        adapterM.notifyDataSetChanged()
        if(mediaV.noteId!=-1.toLong()){
            addNoteViewModel.deleteMultimedia(mediaV)
        }else{
            media.remove(mediaV)
        }
        mediasList.remove(mediaV)
        adapterM.setData(mediasList)
        adapterM.notifyDataSetChanged()
    }

    override fun onEditClickListener(mediaV: Multimedia) {
        TODO("Not yet implemented")
    }
}


