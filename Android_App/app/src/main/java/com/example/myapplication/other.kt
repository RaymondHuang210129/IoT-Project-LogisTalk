package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button

class other : AppCompatActivity() {
    var btn_accept: Button? = null
    var username: String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        btn_accept = findViewById(R.id.btn_reset)
        btn_accept?.setOnClickListener { accept() }

        username = intent?.getStringExtra(
            "DRIVER_ID");
        val wv: WebView = findViewById(R.id.wv)
        val url = "http://140.113.123.112:7788/index?name="+username
        wv.loadUrl(url)

    }

    private  fun accept(){

        val wv: WebView = findViewById(R.id.wv)
        val url = "http://140.113.123.112:7788/index?name="+username
        wv.loadUrl(url)
/*
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        startActivity(intent)*/
    }
}
