package com.example.proyectofinalv2.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.MainViewModelFactory
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.ActivityAddNoteBinding
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    val photos = mutableListOf<Multimedia>();
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    lateinit var currentVideoPath: String
    lateinit var photoURI: Uri
    private lateinit var binding: ActivityAddNoteBinding
    private var note: Note? = null;
    private val addNoteViewModel: MainViewModel by  viewModels {
        MainViewModelFactory((application as NoteApp).database!!.noteDao(), (application as NoteApp).database!!.mediaDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dueDateWrapper.visibility = View.GONE
        binding.cancelar.setOnClickListener{finish()}
        binding.crear.setOnClickListener{createNote()}
        binding.fotoBtn.setOnClickListener{addPhoto()}
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
            photos.add(Multimedia(noteId = -1, type = REQUEST_IMAGE_CAPTURE.toLong(), path = photoURI.toString()));
            Toast.makeText(this, photos.toString(), Toast.LENGTH_LONG).show()
        }
    }



    private fun createNote() {
        val title = binding.titleEditView.text.toString()
        val description = binding.descriptionEditView.text.toString()
        val isATask = binding.isTaskSwitch.isChecked
        var dueDate: Date?
        if (isATask){
            val dueDateStr = binding.dueDate.text.toString()
            val dueDateStrAux = dueDateStr.substring(6)+"-"+dueDateStr.substring(0, 2)+"-"+dueDateStr.substring(3, 5)
            val dueDateAux = LocalDate.parse(dueDateStrAux)
            Toast.makeText(this,dueDateStrAux, Toast.LENGTH_LONG)
            dueDate = localDateToDate(dueDateAux)!!
        }else dueDate = null


        // TODO: Add reminders and media
        if(note==null){
            val newNote = Note(title = title, description = description, isTask = isATask,
                dateCreation = localDateToDate(LocalDate.now()), dueDate = dueDate, isComplete = false, dateCompleted = null)
            addNoteViewModel.insertNewNote(newNote, photos)
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



    fun localDateToDate(localDate: LocalDate): Date? {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }
}