package com.generlas.winterexam.model

import android.content.Context
import android.util.Log
import com.generlas.winterexam.contract.SignContract
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * description ： TODO:SignActivity的Model
 * date : 2025/2/7 21:27
 */
class SignModel(val context: Context) : SignContract.model {
    override fun sign(username: String, password: String, rePassword: String, callback: Callback) {
        val url = "https://www.wanandroid.com/user/register"
        val signData =
            mapOf("username" to username, "password" to password, "repassword" to rePassword)
        val httpUtil = HttpUtil()
        httpUtil.Http_Post(url, signData, callback)
    }
}