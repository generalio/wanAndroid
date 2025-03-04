package com.generlas.winterexam.model

import android.content.Context
import com.generlas.winterexam.contract.MainContract
import okhttp3.Callback

/**
 * description ： TODO:MainActivity的Model
 * date : 2025/2/6 17:40
 */
class MainModel(private val context: Context) : MainContract.Model {

    //自动登录
    override fun autoLogin(username: String, password: String, callback: Callback) {
        val urlLogin: String = "https://www.wanandroid.com/user/login"
        if (username != "" && password != "") {
            val userData = mapOf("username" to username, "password" to password)
            val httpUtil = HttpUtil(context)
            httpUtil.Http_Post(urlLogin, userData, callback)
        }
    }

    //登出
    override fun logout(callback: Callback) {
        val url = "https://www.wanandroid.com/user/logout/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url, callback)
    }

    //保存用户信息
    fun saveUserInfo(user: UserInfo) {
        val sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit()
        sharedPreferences.putString("username", user.username)
        sharedPreferences.putString("nickname", user.nickname)
        sharedPreferences.putInt("id", user.id)
        sharedPreferences.putInt("coinCount", user.coinCount)
        sharedPreferences.putString("email", user.email)
        sharedPreferences.apply()
    }

    //清除用户信息
    fun clearUserInfo() {
        val sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit()
        sharedPreferences.clear().apply()
    }
}