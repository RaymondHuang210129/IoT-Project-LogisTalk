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
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class ConnThread constructor(val activity: Activity, val account: String, val password: String): Thread() {

    override fun run() {
        super.run()

        activity.findViewById<TextView>(R.id.tv_loc).text = "start"
        var reqParam = URLEncoder.encode("account", "UTF-8") + "=" + URLEncoder.encode(account, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")

        try {

            val urlstr= "http://140.113.123.90:9527/appLogin"

            val url = URL(urlstr)
            val conn: HttpURLConnection = url.openConnection() as
                    HttpURLConnection

            var statusCode = 0
            do {
                conn.requestMethod = "POST"
                conn.connectTimeout = 5000
                conn.setDoOutput(true)

                conn.setChunkedStreamingMode(0);
                val wr = OutputStreamWriter(conn.getOutputStream());
                wr.write(reqParam);
                wr.flush()

                conn.connect()
                statusCode = conn.getResponseCode()



            } while (statusCode != 200)

            if(statusCode == 200){
                val resStr = getJSONEntity(conn)
                Log.d("OAO",resStr)
                val status = JSONObject(resStr).getString("message")
                val driverID = JSONObject(resStr).getInt("driverID")
                val token = JSONObject(resStr).getString("token")
                activity.findViewById<TextView>(R.id.tv_loc).text = status
                activity.findViewById<TextView>(R.id.tv_ny).text = driverID.toString()
                activity.findViewById<TextView>(R.id.tv_token).text = token
                Log.d("OAO",status)

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
        activity.findViewById<TextView>(R.id.tv_loc).text = "GG"
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