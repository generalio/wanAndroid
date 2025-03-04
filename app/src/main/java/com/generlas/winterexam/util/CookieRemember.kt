package com.generlas.winterexam.util

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File

/**
 * description ： TODO:持久化保存账号密码
 * date : 2025/3/4 09:03
 */
class CookieRemember(context: Context): CookieJar {

    private val cookies: MutableList<Cookie> = mutableListOf()
    private val sharedPreferences = context.getSharedPreferences("Cookies", Context.MODE_PRIVATE)
    private val list: Map<String, *> = sharedPreferences.all

    init {
        for((key, value) in list) {
            val cookieValue = value as String
            val cookie = Cookie.parse("https://www.wanandroid.com".toHttpUrl(), cookieValue)
            if(cookie != null) {
                cookies.add(cookie)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookies
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies.addAll(cookies)

        val editor = sharedPreferences.edit()
        for(cookie in this.cookies) {
            editor.putString(cookie.name, cookie.toString())
        }
        editor.apply()
    }

}