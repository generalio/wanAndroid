package com.generlas.winterexam.model

import android.content.Context
import android.util.Log
import com.generlas.winterexam.contract.SearchContract
import okhttp3.Callback

/**
 * description ： TODO:SearchModel的Model
 * date : 2025/2/9 00:23
 */
class SearchModel(private val context: Context) : SearchContract.model {
    //获取搜索信息
    override fun getSearchInfo(k: String, page: Int, callback: Callback) {
        val url = "https://www.wanandroid.com/article/query/$page/json"
        val httpUtil = HttpUtil(context)
        val searchData = mapOf("k" to k)
        httpUtil.Http_Post(url, searchData, callback)
    }
}