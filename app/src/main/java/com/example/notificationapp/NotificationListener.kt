package com.example.notificationapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


val NOTIFICATION_PACKAGE = "com.example.notificationapp.NotificationListener"

class NotificationListener(
    val TAG: String = "Notification Listener"
) : NotificationListenerService() {


    lateinit var nlReceiver: NotificationReceiver

    override fun onCreate() {
        super.onCreate()
        nlReceiver = NotificationReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(NOTIFICATION_PACKAGE)
        registerReceiver(nlReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(nlReceiver)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.i(TAG, "********* Notification Posted")
        Log.i(TAG, "ID :"+sbn?.id+"\t"+sbn?.notification?.tickerText+"\t"+sbn?.packageName)
        var intent = Intent(NOTIFICATION_PACKAGE)
        intent.putExtra("notification_event","onNotificationPosted : "+sbn?.packageName + "\n")
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.i(TAG, "********* Notification Removed")
        Log.i(TAG, "ID :"+sbn?.id+"\t"+sbn?.notification?.tickerText+"\t"+sbn?.packageName)
        var intent = Intent(NOTIFICATION_PACKAGE)
        intent.putExtra("notification_event","onNotificationRemoved : "+sbn?.packageName + "\n")
        sendBroadcast(intent)
    }



    class NotificationReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.getStringExtra("command").equals("clearall")){
                NotificationListener().cancelAllNotifications()
            } else if(intent?.getStringExtra("command").equals("list")){
                val intent = Intent(NOTIFICATION_PACKAGE)
                intent.putExtra("notification_event", "=================")

                context?.sendBroadcast(intent)
                var i = 1
                for(sbn: StatusBarNotification in NotificationListener().activeNotifications){
                    var intentTemp = Intent(NOTIFICATION_PACKAGE)
                    intentTemp.putExtra("notification_event", i.toString()+" "+sbn.packageName+"\n")
                    context?.sendBroadcast(intentTemp)

                }
                context?.sendBroadcast(intent)
            }
        }

    }

}