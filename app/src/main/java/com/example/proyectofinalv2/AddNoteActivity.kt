package com.example.proyectofinalv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.NavController
import com.example.proyectofinalv2.databinding.ActivityAddNoteBinding
import com.example.proyectofinalv2.databinding.ActivityMainBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dueDateWrapper.visibility = View.GONE
        binding.cancelar.setOnClickListener{finish()}
        binding.crear.setOnClickListener{createNote()}
        binding.isTaskSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                binding.dueDateWrapper.visibility = View.VISIBLE
            }else{
                binding.dueDateWrapper.visibility = View.GONE
            }
        }
    }

    private fun createNote() {
        val title = binding.titleEditView.text.toString()
        val description = binding.descriptionEditView.text.toString()
        val isATask = binding.isTaskSwitch.isActivated
        val dueDate = binding.titleEditView.text.toString()
    }
}