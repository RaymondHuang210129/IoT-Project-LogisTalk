package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.myapplication.com.example.myapplication.ConnThread3

class OrderActivity : AppCompatActivity() {

    lateinit var lv : ListView
    var driverId: Int ?= null
    var token: String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        driverId = intent?.getIntExtra(
            "DRIVER_ID",0);
        token = intent?.getStringExtra("TOKEN")
        Log.d("Operating",driverId.toString())
        lv = findViewById(R.id.lv)
        try {
            send()
        } catch (e: Exception) {}
    }

    fun send() {

        val thread = ConnThread3(this, lv, token, driverId)
        thread.start()
    }
}
