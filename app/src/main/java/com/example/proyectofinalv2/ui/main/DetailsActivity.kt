package com.example.proyectofinalv2.ui.main

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.MainViewModel
import com.example.proyectofinalv2.MainViewModelFactory
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.ActivityDetailsBinding
import com.example.proyectofinalv2.domain.model.Multimedia
import com.example.proyectofinalv2.domain.model.Note
import com.example.proyectofinalv2.domain.model.Reminder
import java.io.IOException
import java.util.ArrayList

class DetailsActivity : AppCompatActivity() {
    private var note: Note? = null;
    private var mediasList = ArrayList<Multimedia>()
    private var remindersList = ArrayList<Reminder>()
    private lateinit var binding: ActivityDetailsBinding
    // Audio
    private lateinit var recorder: MediaRecorder
    private var player: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false
    private var idPlaying: Long = 0

    private val viewModel: MainViewModel by  viewModels {
        MainViewModelFactory((application as NoteApp).database!!.noteDao(),
            (application as NoteApp).database!!.mediaDao(), (application as NoteApp).database!!.reminderDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        note = intent.getSerializableExtra("note") as Note?
        if(note!=null){
            binding.apply {
                titleTextView.setText(note!!.title)
                descriptionTextView.setText(note!!.description)
                dateCreatedTextView.setText(note!!.dateCreation.toString())
                dueDateTextView.setText(note!!.dueDate.toString())
            }
            if(note!!.isTask == true){
                if(note!!.isComplete == true){
                    binding.completeImageView.setImageResource(R.drawable.ic_baseline_check_circle_24)
                }else{
                    binding.completeImageView.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                }
                binding.dueDateTextView.visibility = View.VISIBLE
                binding.dueDateTextView1.visibility = View.VISIBLE
            }else{
                binding.completeImageView.visibility = View.GONE
                binding.dueDateTextView.visibility = View.GONE
                binding.dueDateTextView1.visibility = View.GONE
            }
            if(intent.hasExtra("media")){
                mediasList = intent.getSerializableExtra("media") as ArrayList<Multimedia>;
                getMedia()
            }
            if(intent.hasExtra("reminder")){
                remindersList = intent.getSerializableExtra("reminder") as ArrayList<Reminder>;
                getReminder()
            }

        }
    }

    private fun getReminder() {
        if(note!=null && remindersList!=null){

            for (reminder in remindersList){
                if(reminder.noteId == note!!.id){
                    val textView = TextView(binding.remindersLayout.context)
                    textView.setText(reminder.date.toString())
                    binding.remindersLayout.addView(textView);
                }
            }
        }
    }

    private fun getMedia() {
        if(note!=null && mediasList!=null){
            for (media in mediasList){
                if(media.type == 1.toLong() && media.noteId == note!!.id){
                    val imageView = ImageView(binding.photoLayout.context)
                    imageView.layoutParams = LinearLayout.LayoutParams(800, 800)
                    Glide.with(binding.photoLayout.context)
                        .load(media.path)
                        .fitCenter()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.placeholder)
                        .into(imageView);
                    binding.photoLayout.addView(imageView);
                }else if(media.type == 2.toLong() && media.noteId == note!!.id){
                    val videoView = VideoView(this)
                    videoView.layoutParams = LinearLayout.LayoutParams(800, 800)
                    videoView.setVideoPath(media.path)
                    videoView.start()
                    val mediacontrolleralone = MediaController(this)
                    mediacontrolleralone.setAnchorView(videoView)
                    videoView.setMediaController(mediacontrolleralone)
                    binding.videosLayout.addView(videoView);

                }else  if(media.type == 3.toLong() && media.noteId == note!!.id){
                    val button = ImageView(this)
                    button.layoutParams = LinearLayout.LayoutParams(200, 200)
                    button.setImageResource(R.drawable.ic_play)
                    button.setOnClickListener {
                        if(media.id!=idPlaying){
                            isPlaying=false
                            for(viewV in binding.audiosLayout.children){
                                (viewV as ImageView).setImageResource(R.drawable.ic_play)
                            }
                        }
                        idPlaying = media.id
                        if(!isPlaying) button.setImageResource(R.drawable.ic_playing)
                        else button.setImageResource(R.drawable.ic_play)
                        onPlay(media.path, isPlaying)
                        isPlaying = !isPlaying
                    }
                    binding.audiosLayout.addView(button);
                }
            }
        }
    }

    private fun onPlay(path: String, start: Boolean) = if (start) {
        stopPlaying()
    } else {
        startPlaying(path)
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startPlaying(path: String) {
        player = MediaPlayer().apply {
            try {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(path)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("Audio error", "prepare() failed")
            }
        }
    }

}