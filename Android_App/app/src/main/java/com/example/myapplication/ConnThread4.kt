package com.example.myapplication.com.example.myapplication

import android.app.Activity
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

class ConnThread4 constructor(val activity: Activity, val lv: ListView): Thread() {

    override fun run() {
        super.run()
        // activity.findViewById<TextView>(R.id.textView).text = "loading........"
//        activity.run { Toast.makeText(activity, "155",
//                Toast.LENGTH_SHORT).show() }
        try {

            val urlstr= "http://www.hy0936.com.tw/Dongmen/data.json"

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
//                activity.run { Toast.makeText(activity, "200",
//                        Toast.LENGTH_SHORT).show() }
                val resStr = getJSONEntity(conn)
//                activity.run { Toast.makeText(activity, resStr,
//                        Toast.LENGTH_SHORT).show() }
                if (resStr != null) {
                    val res_arr = JSONArray(resStr)
                    val res_list = ArrayList<Order>()
                    for (i in 0 .. res_arr.length() - 1) {
                        val name = res_arr.getJSONObject(i).getString("name")
                        val pic = res_arr.getJSONObject(i).getString("pic")
                        val rate = res_arr.getJSONObject(i).getDouble("rate")

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
            //val adapter = MyAdapter(activity, al,token,driveID)
         //   lv.adapter = adapter
       //     adapter.setNotifyOnChange(true)
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