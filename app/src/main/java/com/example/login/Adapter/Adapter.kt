package com.example.login.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login.R
import com.example.login.model.Data

class Adapter(private var datalist:ArrayList<Data>):RecyclerView.Adapter<Adapter.ViewHolder>() {



    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val textView=itemView.findViewById<TextView>(R.id.textView3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
       val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {

        val currentlist=datalist[position]
         holder.textView.text=currentlist.data
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}