package com.example.proyectofinalv2

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.bumptech.glide.util.Util

class RebootServiceClass(name: String?) : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        val intentType = intent?.extras?.getString("caller")
        if(intentType == null) return;
        if(intentType.equals("RebootReceiver")){
            val settings = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
            //Utils
        }
    }

}