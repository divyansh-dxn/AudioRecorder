package com.dxn.recorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class AudioListAdapter(val allFiles: Array<File>, val clickListener: (File, Int) -> Unit) :
    RecyclerView.Adapter<AudioListAdapter.AudioListViewHolder>() {

    class AudioListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listTitle = itemView.findViewById<TextView>(R.id.list_title)
        val listDate = itemView.findViewById<TextView>(R.id.list_date)
        val listItemContainer = itemView.findViewById<ConstraintLayout>(R.id.list_item_container)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return AudioListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioListViewHolder, position: Int) {
        holder.listItemContainer.setOnClickListener { clickListener(allFiles.get(position),position) }
        holder.listTitle.text = allFiles.get(position).name
        holder.listDate.text = TimeAgo.getTimeAgo(allFiles.get(position).lastModified())
    }

    override fun getItemCount(): Int {
        return allFiles.size
    }


}