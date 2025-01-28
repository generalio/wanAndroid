package com.generlas.winterexam.util

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * description ： TODO:OkHttp封装
 * date : 2025/1/26 16:50
 */
class HttpUtil {

    //get请求
    fun Http_Get(url: String, callback: okhttp3.Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }

    //post请求
    fun Http_Post(url: String, data: Map<String, String>, callback: okhttp3.Callback) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .apply {
                for ((key, value) in data) {
                    add(key, value)
                }
            }
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(callback)
    }
}