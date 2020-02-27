package com.example.myapplication.com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myapplication.ConnThread
import com.example.myapplication.ConnThread5

import com.example.myapplication.R


class MyAdapter(val c: Context, val itemarr: ArrayList<Order>, val token:String?, val driveID:Int?)
    : ArrayAdapter<Order>(c, 0, itemarr) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(c)
        var itemlayout: LinearLayout? = null
        if (convertView == null) {
            itemlayout = inflater.inflate(R.layout.mylist, null)
                    as? LinearLayout
        } else {
            itemlayout = convertView as? LinearLayout
        }
        val item: Order? = getItem(position)
        val tv_address = itemlayout?.findViewById(R.id.tv_address) as TextView
        val tv_price = itemlayout?.findViewById(R.id.tv_price) as TextView
        val imageView = itemlayout?.findViewById(R.id.iv_pic) as ImageView
        val btn_accept= itemlayout?.findViewById(R.id.btn_accept) as Button
        btn_accept?.setOnClickListener { OAO(itemarr[position].orderID.toString(),token,driveID) }

        //val ratebar = itemlayout?.findViewById(R.id.rb_rate) as RatingBar

        tv_address.text = itemarr[position].address
        tv_price.text = "$"+itemarr[position].orderPrice.toString()
        //DownloadImageTask(imageView).execute(itemarr[position].pic)
        //ratebar.rating = itemarr[position].rate.toFloat()

        return itemlayout
       // return super.getView(position, convertView, parent)
    }
    private fun OAO(msg: String, token:String?, driverID:Int?) {
        Toast.makeText(getContext(), msg+token.toString()+driveID.toString(), Toast.LENGTH_SHORT).show();
        val thread = ConnThread5( token, driverID, msg)
        thread.start()
        thread.join()


    }
}

private class DownloadImageTask(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        var bmp: Bitmap? = null
        try {
            val input = java.net.URL(urldisplay).openStream()
            bmp = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }

        return bmp
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            bmImage.setImageBitmap(result)
        }
    }
}