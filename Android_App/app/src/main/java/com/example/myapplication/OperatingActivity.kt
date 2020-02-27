package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_operating.*

class OperatingActivity : AppCompatActivity() {
    var btn_accept: Button? = null
    var btn_complete: Button? = null
    var driverId: Int ?= null
    var token: String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operating)
        driverId = intent?.getIntExtra(
            "DRIVER_ID",87);

        token = intent?.getStringExtra("TOKEN")
        btn_accept = findViewById(R.id.btn_accept)
        btn_accept?.setOnClickListener { accept() }

        btn_complete = findViewById(R.id.btn_complete)
        btn_complete?.setOnClickListener { complete() }

        Log.d("Operating",driverId.toString())

    }
    private fun accept(){
        var intent2 = Intent()
        intent2.setClass(this, OrderActivity::class.java)
        intent2.putExtra("DRIVER_ID", driverId);
        intent2.putExtra("TOKEN",token)
        startActivity(intent2)
    }
    private fun complete(){
        var intent2 = Intent()
        intent2.setClass(this, CheckOrderActivity::class.java)
        intent2.putExtra("DRIVER_ID", driverId);
        intent2.putExtra("TOKEN",token)
        startActivity(intent2)
    }

}
