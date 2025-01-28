package com.generlas.winterexam.view.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.generlas.winterexam.R
import com.generlas.winterexam.repository.model.CarouselInfo
import com.generlas.winterexam.view.activity.WebViewActivity

/**
 * description ： 轮播图的Adapter
 * date : 2025/1/27 23:21
 */
class CarouselViewPager2Adapter(private val context: Context, private val passageData: List<CarouselInfo>) :
    RecyclerView.Adapter<CarouselViewPager2Adapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val carouselImage: ImageView = view.findViewById(R.id.iv_home_carousel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_carousel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val passage = passageData[position]
        val passageUrl = passage.imagePath
        //使用glide加载网络资源图片
        Glide.with(holder.carouselImage.context)
            .load(passageUrl)
            .into(holder.carouselImage)

        //设置图片的点击事件
        holder.carouselImage.setOnClickListener {
            val Url = passageData[position].url
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", Url)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return passageData.size
    }


}