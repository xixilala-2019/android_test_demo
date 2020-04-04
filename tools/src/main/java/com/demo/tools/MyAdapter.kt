package com.demo.tools

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

/**
 * Created by hc on 2019.8.1.
 */
class MyAdapter (activity : AppCompatActivity): Adapter<MyAdapter.HH>() {

    var dataArray : Array<String>
    var activity: AppCompatActivity ? = activity

    init {
        dataArray = getData()
    }


    fun getData() : Array<String> {
        return activity?.resources?.getStringArray(R.array.names) as Array<String>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HH {
        return activity?.layoutInflater?.inflate(R.layout.item_tool,parent,false)?.let { HH(it) }!!
    }

    override fun getItemCount(): Int {
        Log.e("", "----size----" + (dataArray.size ?: 0))
        return dataArray.size ?: 0
    }

    override fun onBindViewHolder(holder: HH, position: Int) {
        holder.tvText.text = dataArray[position]

        holder.itemView.setOnClickListener {

            if (position == 0) {
                val intent = Intent()
                intent.setClassName("enfc.metro", "enfc.metro.main.MainActivity")
                activity?.startActivity(intent)
            } else if (position == 1) {

                activity?.startActivity(Intent(activity, MediaDealActivity::class.java))

            }

        }
    }


    class HH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvText:TextView = itemView.findViewById(R.id.tvText)
    }
}