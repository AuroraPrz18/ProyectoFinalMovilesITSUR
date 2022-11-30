package com.example.proyectofinalv2.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import java.util.ArrayList

class DetailsActivity : AppCompatActivity() {
    private var note: Note? = null;
    private var mediasList = ArrayList<Multimedia>()
    private var remindersList = ArrayList<Reminder>()
    private lateinit var binding: ActivityDetailsBinding
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
                }
            }
        }
    }

}