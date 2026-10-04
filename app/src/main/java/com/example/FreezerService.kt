package com.example

import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class FreezerService : Service() {
    companion object {
        const val ACTION_MANUAL_FREEZE = "com.example.ACTION_MANUAL_FREEZE"
    }

    private val repository by lazy { AppRepository(this) }
    
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF || intent.action == ACTION_MANUAL_FREEZE) {
                freezeApps()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(ACTION_MANUAL_FREEZE)
        }
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun freezeApps() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packagesToFreeze = repository.getSelectedPackages()
        
        for (packageName in packagesToFreeze) {
            try {
                am.killBackgroundProcesses(packageName)
            } catch (e: Exception) {
                // Skip if error occurs
            }
        }
    }
}
