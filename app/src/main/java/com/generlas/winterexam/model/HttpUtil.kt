package com.generlas.winterexam.model

import android.content.Context
import com.generlas.winterexam.util.CookieRemember
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * description ： TODO:OkHttp封装
 * date : 2025/1/26 16:50
 */
class HttpUtil(context: Context) {

    val cookieJar = CookieRemember(context)

    //get请求
    fun Http_Get(url: String, callback: okhttp3.Callback) {
        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(callback)
    }

    //post请求
    fun Http_Post(url: String, data: Map<String, String>, callback: okhttp3.Callback) {
        val client = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
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