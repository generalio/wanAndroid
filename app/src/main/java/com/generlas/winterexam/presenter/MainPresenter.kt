package com.generlas.winterexam.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.generlas.winterexam.contract.MainContract
import com.generlas.winterexam.model.MainModel
import com.generlas.winterexam.model.UserInfo
import com.generlas.winterexam.view.activity.LoginActivity
import com.generlas.winterexam.view.activity.MainActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * description ： TODO:MainActivity的Presenter
 * date : 2025/2/6 18:08
 */
class MainPresenter(private val view: MainActivity,private val model: MainModel) : MainContract.Presenter {

    //处理自动登录
    override fun onLogin() {
        val sharedPreferences = view.getSharedPreferences("Cookies", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "").toString()
        val password = sharedPreferences.getString("password", "").toString()
        if(username != "" && password != "") {
            model.autoLogin(username, password, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    view.showError(e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(responseData).asJsonObject
                    val userData = jsonObject.getAsJsonObject("data")
                    val user = gson.fromJson(userData, UserInfo::class.java)
                    view.runOnUiThread {
                        model.saveUserInfo(user)
                        view.showInfo(user.username, user.coinCount)
                    }
                }

            })
        }
    }

    //处理登出
    override fun onLogout() {
        model.logout(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val jsonObject = JSONObject(responseData)
                val errorCode = jsonObject.getInt("errorCode")
                if (errorCode == 0) {
                    (view as Activity).runOnUiThread {
                        model.clearUserInfo()
                        view.showInfo("未登录", -1)
                        view.showLogout()
                    }
                }
            }

        })
    }

}