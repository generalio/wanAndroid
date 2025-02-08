package com.generlas.winterexam.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.view.activity.WebViewActivity

/**
 * description ： Navigation内容的Adapter
 * date : 2025/2/1 22:25
 */
class NavigationContentAdapter(val context: Context, val linkInfo: List<PassageInfo>) :
    RecyclerView.Adapter<NavigationContentAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val linkName: TextView = view.findViewById(R.id.tv_navigation_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.navigation_content_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.linkName.text = linkInfo[position].title
        holder.itemView.setOnClickListener {
            val url = linkInfo[position].link
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return linkInfo.size
    }
}