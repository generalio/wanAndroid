package com.generlas.winterexam.presenter

import android.content.Context
import com.generlas.winterexam.contract.BaseContract
import com.generlas.winterexam.contract.LoginContract
import com.generlas.winterexam.model.LoginModel
import com.generlas.winterexam.model.UserInfo
import com.generlas.winterexam.view.activity.LoginActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * description ： TODO:LoginActivity的Presenter
 * date : 2025/2/6 23:07
 */
class LoginPresenter(private val view: LoginActivity, private val model: LoginModel) : LoginContract.Presenter {

    //处理登录
    override fun onLogin(username: String, password: String) {
        model.login(username, password, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                 view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val (errorCode, errorMsg) = checkIsSucceed(responseData.toString())
                if (errorCode != -1) {
                    //解析json数据
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(responseData).asJsonObject
                    val userData = jsonObject.getAsJsonObject("data")
                    val user = gson.fromJson(userData, UserInfo::class.java)
                    model.userInfoSave(user)
                    view.loginSucceed(user)
                } else {
                    view.loginFailed()
                }
            }

        })
    }

    //检测是否登陆成功并返回错误代码和信息
    private fun checkIsSucceed(jsonData: String): Pair<Int, String> {
        val jsonObject = JSONObject(jsonData)
        val errorCode = jsonObject.getInt("errorCode")
        val errorMsg = jsonObject.getString("errorMsg")
        return Pair(errorCode, errorMsg)
    }

    //初始化输入框
    override fun initInfo() : Pair<String,String> {
        val output = view.getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username: String = output.getString("username", "").toString()
        val password: String = output.getString("password", "").toString()
        return Pair(username, password)
    }

    //记住密码功能
    override fun passwordRemember(username: String, password: String) {
        val editor = view.getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()
    }
}