package com.generlas.winterexam.model

import android.content.Context
import com.generlas.winterexam.contract.LoginContract
import okhttp3.Callback

/**
 * description ： TODO:类的作用
 * date : 2025/2/6 22:16
 */
class LoginModel(private val context: Context) : LoginContract.Model{

    //请求登录
    override fun login(username: String, password: String,callback: Callback) {
        val urlLogin: String = "https://www.wanandroid.com/user/login"
        val userData = mapOf("username" to username, "password" to password)
        val httpUtil = HttpUtil()
        httpUtil.Http_Post(urlLogin, userData, callback)
    }

    //保存用户基本信息
    override fun userInfoSave(user: UserInfo) {
        val sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit()
        sharedPreferences.putString("username", user.username)
        sharedPreferences.putString("nickname", user.nickname)
        sharedPreferences.putInt("id", user.id)
        sharedPreferences.putInt("coinCount", user.coinCount)
        sharedPreferences.putString("email", user.email)
        sharedPreferences.apply()
    }

}