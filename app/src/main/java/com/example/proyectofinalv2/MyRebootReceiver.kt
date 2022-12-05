package com.example.proyectofinalv2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class MyRebootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, RebootServiceClass::class.java);
        serviceIntent.putExtra("caller", "RebootReceiver")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context?.startForegroundService(serviceIntent)
        }else{
            context?.startService(serviceIntent)
        }
    }

}