package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.myapplication.com.example.myapplication.ConnThread3
import com.example.myapplication.com.example.myapplication.ConnThread6

class CheckOrderActivity : AppCompatActivity() {

    lateinit var lv : ListView
    var driverId: Int ?= null
    var token: String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_order)
        driverId = intent?.getIntExtra(
            "DRIVER_ID",0);
        token = intent?.getStringExtra("TOKEN")
        Log.d("Operating",driverId.toString())
        lv = findViewById(R.id.check_lv)
        try {
            send()
        } catch (e: Exception) {}
    }

    fun send() {

        val thread = ConnThread6(this, lv, token, driverId)
        thread.start()
    }
}
