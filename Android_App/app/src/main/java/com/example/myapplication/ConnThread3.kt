package com.example.myapplication.com.example.myapplication

import android.app.Activity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONObject
import android.widget.Toast
import com.example.myapplication.R
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ConnThread3 constructor(val activity: Activity, val lv: ListView, val token: String?,val driveId:Int?): Thread() {

    override fun run() {
        super.run()
       // activity.findViewById<TextView>(R.id.textView).text = "loading........"
//        activity.run { Toast.makeText(activity, "155",
//                Toast.LENGTH_SHORT).show() }
        try {
           // val token = activity.findViewById<TextView>(R.id.tv_token).text
            val urlstr= "http://140.113.123.90:9527/appGetOrders?token="+token
            Log.d("OAO",urlstr)
            val url = URL(urlstr)
            val conn: HttpURLConnection = url.openConnection() as
                    HttpURLConnection

            var statusCode = 0
            do {
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.connect()
                statusCode = conn.getResponseCode()
            } while (statusCode != 200)

            if(statusCode == 200){

                val resStr = getJSONEntity(conn)

                if (resStr != null) {

                    val status = JSONObject(resStr).getString("message")
                    Log.d("OAO",status)
                    if(status != "valid token"){
                        return
                    }
                    val order_arr = JSONArray(JSONObject(resStr).getString("orders"))
                    val res_list = ArrayList<Order>()
                    for (i in 0 .. order_arr.length() - 1) {
                        val address = order_arr.getJSONObject(i).getString("address")
                        val orderID = order_arr.getJSONObject(i).getInt("orderID")
                        val orderPrice = order_arr.getJSONObject(i).getInt("orderPrice")
                        res_list.add(Order(orderID, address, orderPrice))
                    }
                    connsucc(res_list)
                    return
                }
            }
        } catch (e: Exception) {
        }
        connerror()
    }

    fun connsucc(al: ArrayList<Order>) {
        activity.runOnUiThread {
            val adapter = MyAdapter(activity, al ,token,driveId )
            lv.adapter = adapter
            adapter.setNotifyOnChange(true)
        }
    }

    fun connerror() {
//        activity.run { Toast.makeText(activity, "連線失敗",
//                Toast.LENGTH_SHORT).show() }
        activity.findViewById<TextView>(R.id.textView).text = "連線失敗"
    }

    fun getJSONEntity(conn: HttpURLConnection): String? {
        try {
            val inputstream = conn.inputStream
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len = 0
            do {
                len = inputstream.read(buffer)
                if (len > 0) {
                    baos.write(buffer, 0, len)
                }
            } while (len > 0)
            val jsonString = baos.toString()
            baos.close()
            inputstream.close()
            return jsonString
        } catch (e: Exception) {
        }
        return null
    }

}