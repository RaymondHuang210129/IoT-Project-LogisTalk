package com.example.myapplication.com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myapplication.*


class MyAdapter2(val c: Context, val itemarr: ArrayList<Order>, val token:String?, val driveID:Int?)
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
        btn_accept.text="完成"
        btn_accept?.setOnClickListener { OAO(itemarr[position].orderID.toString(),token,driveID) }



        tv_address.text = itemarr[position].address
        tv_price.text = "$"+itemarr[position].orderPrice.toString()


        return itemlayout

    }
    private fun OAO(msg: String, token:String?, driverID:Int?) {
        Toast.makeText(getContext(), msg+token.toString()+driveID.toString(), Toast.LENGTH_SHORT).show();
        val thread = ConnThread7( token, driverID, msg)
        thread.start()
        thread.join()
        var intent2 = Intent()
        intent2.setClass(c, CheckOrderActivity::class.java)
        intent2.putExtra("DRIVER_ID", driverID);
        intent2.putExtra("TOKEN",token)
    }
}
