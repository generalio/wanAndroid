package com.generlas.winterexam.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.model.NavigationInfo

/**
 * description ： TODO:导航列表的Adapter
 * date : 2025/2/1 18:40
 */
class NavigationListAdapter(
    val context: Context,
    val navigationInfo: List<NavigationInfo>,
    val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NavigationListAdapter.ViewHolder>() {

    var nowPosition: Int = 0

    //设置item的点击事件接口
    interface OnItemClickListener {
        fun onItemClick(navigationItem: NavigationInfo)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listText: TextView = view.findViewById(R.id.tv_navigation_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.navgation_list_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listText.text = navigationInfo[position].name
        holder.itemView.setOnClickListener {
            //holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF"))
            //holder.listText.setTextColor(Color.parseColor("#0099FF"))
            itemClickListener.onItemClick(navigationInfo[position])
            val lastPosition = nowPosition
            nowPosition = position
            notifyItemChanged(position)
            notifyItemChanged(lastPosition)
        }
        if (position != nowPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.listText.setTextColor(Color.parseColor("#FF000000"))
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF"))
            holder.listText.setTextColor(Color.parseColor("#0099FF"))
        }
    }

    override fun getItemCount(): Int {
        return navigationInfo.size
    }
}