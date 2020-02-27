package com.example.myapplication

import android.app.Activity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.json.JSONObject
import android.widget.Toast
import com.example.myapplication.R
import org.json.JSONArray
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL



class ConnThread2 constructor(val msg:String): Thread() {

    override fun run() {
        super.run()




        try {
            val message="{\"data\":[\""+ msg +"\"]}"
            val urlstr= "http://demo.iottalk.tw:9999/FPGPS/FP_Gps_trace_push"
            val postData: ByteArray = message.toByteArray()
            val url = URL(urlstr)
            val conn: HttpURLConnection = url.openConnection() as
                    HttpURLConnection

            var statusCode = 0
            do {
                conn.requestMethod = "PUT"
                conn.connectTimeout = 5000
                conn.setDoOutput(true)
                conn.setRequestProperty("charset", "utf-8")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("password-key", "b3ea45c3-df12-41d4-bdc9-f0711c7b520f")
                conn.setChunkedStreamingMode(0);

                val outputStream: DataOutputStream = DataOutputStream(conn.outputStream)
                outputStream.write(postData)
                outputStream.flush()


                conn.connect()
                statusCode = conn.getResponseCode()



            } while (statusCode != 200)

            if(statusCode == 200){


                return


            }
        } catch (e: Exception) {
        }
        connerror()
    }

    fun connsucc() {
        // tv.setText("OK");
    }

    fun connerror() {

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