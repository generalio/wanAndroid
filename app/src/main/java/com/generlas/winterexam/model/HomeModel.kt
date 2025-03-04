package com.generlas.winterexam.model

import android.content.Context
import com.generlas.winterexam.contract.HomeContract
import okhttp3.Callback

/**
 * description ： TODO:HomeFragment的Model
 * date : 2025/2/7 23:17
 */
class HomeModel(private val context: Context) : HomeContract.model {

    //请求主页文章的数据
    override fun loadPassage(page: Int, callback: Callback) {
        val url = "https://www.wanandroid.com/article/list/$page/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url, callback)
    }

    //请求主页轮播图的数据
    override fun loadCarouselPassage(callback: Callback) {
        val url = "https://www.wanandroid.com/banner/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url,callback)
    }

    override fun checkLogin(callback: Callback) {
        val url = "https://wanandroid.com//user/lg/userinfo/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url, callback)
    }

    override fun collect(id: Int, callback: Callback) {
        val url = "https://www.wanandroid.com/lg/collect/$id/json"
        val httpUtil = HttpUtil(context)
        val collectData = mapOf("id" to id.toString())
        httpUtil.Http_Post(url, collectData, callback)
    }

    override fun unCollect(id: Int, callback: Callback) {
        val url = "https://www.wanandroid.com/lg/uncollect_originId/$id/json"
        val httpUtil = HttpUtil(context)
        val collectData = mapOf("id" to id.toString())
        httpUtil.Http_Post(url, collectData, callback)
    }

}