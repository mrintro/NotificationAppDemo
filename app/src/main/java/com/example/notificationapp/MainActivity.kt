package com.example.notificationapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificationapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var nReceiver: NotificationReceiver
    lateinit var binding : ActivityMainBinding
    private val NOTIFICATION_PACKAGE = "com.example.notificationapp.NotificationListener"





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        nReceiver = NotificationReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(NOTIFICATION_PACKAGE)
        registerReceiver(nReceiver, intentFilter)

        Log.i("Receiver Tag", "Receiver Created")

        binding.btnListNotify.setOnClickListener{
            val i: Intent = Intent(NOTIFICATION_PACKAGE).putExtra("command", "list")
//            Log.i("intent",)
            sendBroadcast(i)
        }

        binding.btnCreateNotify.setOnClickListener{
            val nManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val nComp: NotificationCompat.Builder = NotificationCompat.Builder(this, "01")
                .setContentTitle("Android Notification")
                .setContentText("Notification Here")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val channel = NotificationChannel("01", "Android Notification", NotificationManager.IMPORTANCE_DEFAULT)

            nManager.createNotificationChannel(channel)

            with(NotificationManagerCompat.from(this)){
                notify(1, nComp.build())
            }

        }

        binding.btnClearNotify.setOnClickListener{
            val intent = Intent(NOTIFICATION_PACKAGE).putExtra("command","clearall")
            sendBroadcast(intent)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(nReceiver)
    }

    fun getText() : String {
        return "abc"
//        return binding.textView.text.toString()
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("BroadCastReceive", "MainActivity")
            var temp = intent?.getStringExtra("notification_event") +"\n" + MainActivity().getText()
            MainActivity().setText(temp)
        }

    }

    private fun setText(temp: String) {
    }

}

