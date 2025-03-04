package com.generlas.winterexam.model

import android.content.Context
import com.generlas.winterexam.contract.PublicContract
import okhttp3.Callback

/**
 * description ： TODO:类的作用
 * date : 2025/2/8 22:19
 */
class PublicModel(private val context: Context) : PublicContract.model {
    override fun loadPassage(id: Int, page: Int, callback: Callback) {
        val url = "https://wanandroid.com/wxarticle/list/$id/$page/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url, callback)
    }

    override fun loadAuthor(callback: Callback) {
        val url = "https://wanandroid.com/wxarticle/chapters/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url, callback)
    }
}