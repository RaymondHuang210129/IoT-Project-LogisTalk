package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import 	android.location.LocationManager
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.Criteria
import android.location.Location.distanceBetween
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    var tv_loc: TextView ? = null
    var tv_ny: TextView? = null
    var et_account: EditText? = null
    var et_password: EditText? = null
    var btn_start: Button? = null
    var btn_stop: Button? = null
    var btn_other: Button? = null
    //var locmgr: LocationManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_loc = findViewById(R.id.tv_loc)
        tv_ny = findViewById(R.id.tv_ny)

        et_account = findViewById(R.id.et_account)
        et_password = findViewById(R.id.et_password)


        btn_start = findViewById(R.id.btn_start)
        btn_start?.setOnClickListener { start() }

        btn_stop = findViewById(R.id.btn_stop)
        btn_stop?.setOnClickListener { stop() }

        btn_other = findViewById(R.id.btn_other)
        btn_other?.setOnClickListener { other() }



        /*val wv: WebView = findViewById(R.id.wv)
        val url = "http://www.hy0936.com.tw/test/a.html"
        wv.loadUrl(url)*/
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                1)
        } else {
            btn_start?.visibility = View.VISIBLE
            btn_stop?.visibility = View.VISIBLE
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions:
    Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if ((grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)) {
            btn_start?.visibility = View.VISIBLE
            btn_stop?.visibility = View.VISIBLE
        }
    }



    override fun onDestroy() {
        super.onDestroy()

    }

    private fun start() {


        findViewById<TextView>(R.id.tv_loc).text = "jizzzzzzz"
        findViewById<TextView>(R.id.tv_ny).text = "jizzzzzzz"
        val thread = ConnThread(this, et_account?.getText().toString(), et_password?.getText().toString())
        thread.start()
        thread.join()


        if(findViewById<TextView>(R.id.tv_loc).text == "Login Success"){
            sucess()

        } else if(findViewById<TextView>(R.id.tv_loc).text == "Login Failure") {
            Toast.makeText(this,"account or password wrong",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"something strange",Toast.LENGTH_SHORT).show();
        }



    }
    private fun sucess(){
        val intent = Intent()
        intent.setClass(this, GpsService::class.java)
        intent.putExtra("DRIVER_ID", findViewById<TextView>(R.id.tv_ny).text.toString().toInt());
        startService(intent)

        Toast.makeText(this,"ok in",Toast.LENGTH_SHORT).show();
        var intent2 = Intent()
        intent2.setClass(this, OperatingActivity::class.java)
        intent2.putExtra("DRIVER_ID", findViewById<TextView>(R.id.tv_ny).text.toString().toInt());
        Log.d("Main",findViewById<TextView>(R.id.tv_ny).text.toString());
        intent2.putExtra("TOKEN", findViewById<TextView>(R.id.tv_token).text.toString());
        startActivity(intent2)


    }
    private fun stop() {
        val intent = Intent()
        intent.setClass(this, GpsService::class.java)
        stopService(intent)
    }
    private fun other(){
        var intent2 = Intent()
        intent2.setClass(this, other::class.java)
        intent2.putExtra("DRIVER_ID", findViewById<TextView>(R.id.tv_ny).text.toString());
        Log.d("Main",findViewById<TextView>(R.id.tv_ny).text.toString());
        startActivity(intent2)
    }
}
