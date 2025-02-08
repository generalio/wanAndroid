package com.generlas.winterexam.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.view.activity.WebViewActivity

/**
 * description ： TODO:Passage的Adapter(使用listAdapter)
 * date : 2025/1/28 20:16
 */
class PassageAdapter(private val context: Context) :
    ListAdapter<PassageInfo, PassageAdapter.ViewHolder>(ItemDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val passageAuthor: TextView = view.findViewById(R.id.tv_card_author)
        val passageDate: TextView = view.findViewById(R.id.tv_card_date)
        val passageTitle: TextView = view.findViewById(R.id.tv_card_title)
        val passageChapterName: TextView = view.findViewById(R.id.tv_card_chapterName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passage = getItem(position)
        if(passage.author != "") {
            holder.passageAuthor.text = passage.author
            holder.passageDate.text = passage.niceDate
        } else {
            holder.passageAuthor.text = passage.shareUser
            holder.passageDate.text = passage.niceShareDate
        }
        holder.passageTitle.text = passage.title
        holder.passageChapterName.text = passage.chapterName

        //整个View的点击事件
        holder.itemView.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", passage.link)
            context.startActivity(intent)
        }
    }

}