package com.generlas.winterexam.model

import com.generlas.winterexam.contract.HomeContract
import okhttp3.Callback

/**
 * description ： TODO:HomeFragment的Model
 * date : 2025/2/7 23:17
 */
class HomeModel : HomeContract.model {

    //请求主页文章的数据
    override fun loadPassage(page: Int, callback: Callback) {
        val url = "https://www.wanandroid.com/article/list/$page/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, callback)
    }

    //请求主页轮播图的数据
    override fun loadCarouselPassage(callback: Callback) {
        val url = "https://www.wanandroid.com/banner/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url,callback)
    }

}