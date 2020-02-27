package com.example.myapplication

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat.postDelayed



class GpsService : Service() {

    companion object {
        val CHANNEL_ID = "lin.gps"
        val NOTIFICATION_ID = 1
    }
    var locmgr: LocationManager? = null
    var nowloc: Location ?= null
    var driverId: Int ?= 0
    val handler = Handler()
    val runnable = object : Runnable {
        override fun run() {
            sendGps()
            handler.postDelayed(this, 5000)
        }
    }
    override fun onCreate() {
        //Toast.makeText(getApplicationContext(), "service start", Toast.LENGTH_LONG).show();
        //initLoc();
    }
    private fun initLoc() {
        locmgr = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        var loc: Location? = null
        try {
            loc = locmgr?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (loc == null) {
                loc = locmgr?.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER);
            }
        } catch (e: SecurityException) {
        }

        if (loc != null) {
            updateLocation(loc)
        } else {
            Toast.makeText(getApplicationContext(), "Cannot get location!", Toast.LENGTH_LONG).show();
        }

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider: String? = locmgr?.getBestProvider(
            criteria, true)

        try {
            if (provider != null) {
                locmgr?.requestLocationUpdates(provider,
                    1000, 1f, loclistener)
            } else {
                locmgr?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, 1f, loclistener)
            }
        } catch (e: SecurityException) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locmgr?.removeUpdates(loclistener)
        handler.removeCallbacks(runnable)
    }
    private fun updateLocation(loc: Location){
        nowloc = loc
        //Toast.makeText(getApplicationContext(), "Loc update", Toast.LENGTH_LONG).show();
       //sendGps();
    }
    var loclistener = object: LocationListener {

        override fun onLocationChanged(loc: Location?) {
            if (loc != null) {
                updateLocation(loc)
            } else {
                Toast.makeText(getApplicationContext(), "Cannot get location!", Toast.LENGTH_LONG).show();
                //tv_loc?.setText("Cannot get location!");
            }
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

        override fun onStatusChanged(provider: String?, status: Int,
                                     extras: Bundle?) {
        }
    }

    private  fun sendGps(){
        val msg = StringBuffer()

        msg.append("{\\\"driverId\\\":")
        msg.append(driverId.toString()+",")
        msg.append("\\\"lat\\\":")
        msg.append(nowloc?.getLatitude().toString()+",")
        msg.append("\\\"long\\\":")
        msg.append(nowloc?.getLongitude().toString()+"}")

        val thread2 = ConnThread2( msg.toString())
        thread2.start()
        thread2.join()
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        //Log.d("OAO",msg.toString())
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        driverId = intent?.getIntExtra(
            "DRIVER_ID",0);

        val intent = Intent()
        intent.setClass(this, MainActivity::class.java!!)

        val pi = PendingIntent.getActivity(this,
            0, intent, 0)
        /*var notification: Notification? = null
        try {

            notification = getNotification(this, pi,
                getString(R.string.app_name), "gps gogo")
        } catch (e: Exception) {
        }
        if (notification != null) {
            startForeground(NOTIFICATION_ID, notification)
        }*/
        initLoc()

        handler.postDelayed(runnable, 5000)
        return Service.START_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }
    private fun getNotification(context: Context, pi: PendingIntent,
                                title: String, msg: String): Notification? {

        var notification: Notification? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val channel = NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW)
                channel.setShowBadge(false)
                val notificationManager: NotificationManager =
                    context.getSystemService(NotificationManager::class.java)
                notificationManager!!.createNotificationChannel(channel)
                notification = Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(msg)
                    .setWhen(System.currentTimeMillis())
                    .build()
            } else if (Build.VERSION.SDK_INT >= 16){
                notification = Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setContentIntent(pi)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(msg)
                    .setWhen(System.currentTimeMillis())
                    .build()
            }
        } catch (throwable: Throwable) {
            return null
        }
        return notification
    }
}
