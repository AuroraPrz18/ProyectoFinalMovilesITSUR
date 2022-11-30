package com.example.proyectofinalv2

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.proyectofinalv2.data.NoteApp
import com.example.proyectofinalv2.databinding.ActivityMainBinding
import com.example.proyectofinalv2.ui.main.AddNoteActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.addNoteBtn.setOnClickListener{
            val newActivityIntent = Intent(this, AddNoteActivity::class.java)
            startActivity(newActivityIntent)
        }

        val viewModelFactory = MainViewModelFactory((application as NoteApp).database!!.noteDao(),
            (application as NoteApp).database!!.mediaDao(), (application as NoteApp).database!!.reminderDao())
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

    }




}