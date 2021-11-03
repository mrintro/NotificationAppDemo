package com.example.notificationapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.Tag
import android.os.Binder
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


val NOTIFICATION_PACKAGE = "com.example.notificationapp.NotificationListener"

class NotificationListener(
    private val TAG: String = "NotificationListener"
) : NotificationListenerService() {


    lateinit var nlReceiver: NotificationReceiver
    val binder: IBinder = LocalBinder()
    private var isBound : Boolean = false



    inner class LocalBinder :Binder() {
        fun getService() : NotificationListener = this@NotificationListener
    }

    override fun onBind(intent: Intent?): IBinder? {
        isBound = true
        Log.i("Binder: ","onBind Called")
        var action = intent?.action
        Log.i("Binder", "onBind: $action")
        Log.i("Binder OnService","$SERVICE_INTERFACE")
        return if(SERVICE_INTERFACE == action){
            Log.i(TAG, "Bound to System")
            super.onBind(intent)
        }else{
            Log.i(TAG,"Bound by Application")
            binder
        }

    }


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
            Log.i("BroadCastReceiver","Secondary")

            if(intent==null){
                Log.i("Debug","Intent NULL")
            }
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