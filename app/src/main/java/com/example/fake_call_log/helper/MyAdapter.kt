package com.example.fake_call_log.helper

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fake_call_log.R
import com.example.fake_call_log.model.log
import android.view.View
import android.widget.TextView

class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var itemList: MutableList<log> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_single_contact_element,
            parent, false)
        return  ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (holder)
        {
            is ViewHolder ->
            {
                holder.bind(itemList[position])
            }
        }
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }

    fun add(Log: log)
    {
        itemList.add(0, Log)
        notifyItemInserted(0)
    }


    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val phonenumber = itemView.findViewById<TextView>(R.id.phonenumber)
        val duration = itemView.findViewById<TextView>(R.id.duration)
        val calltype = itemView.findViewById<TextView>(R.id.calltype)
        fun bind(Log: log)
        {
            phonenumber.text =     "Phone Number: "  + Log.number
            duration.text = "Duration: "+Log.duration
            calltype.text = "Type: "+Log.type
        }
    }
}

class SpacingAdapter(private val padding: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = padding
        outRect.left = padding
        outRect.right = padding
    }
}