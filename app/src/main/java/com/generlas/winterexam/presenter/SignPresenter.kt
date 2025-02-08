package com.generlas.winterexam.presenter

import com.generlas.winterexam.contract.SignContract
import com.generlas.winterexam.model.SignModel
import com.generlas.winterexam.view.activity.SignActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * description ： TODO:SignActivity的Presenter
 * date : 2025/2/7 21:29
 */
class SignPresenter(private val view: SignActivity, private val model: SignModel) : SignContract.Presenter {

    override fun onSign(username: String, password: String, rePassword: String) {
        model.sign(username, password, rePassword, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val (errorCode, errorMsg) = isSucceed(responseData)
                if (errorCode == -1) {
                    view.signFailed(errorMsg)
                } else {
                    val jsonObject = JSONObject(responseData)
                    val dataObject = jsonObject.getJSONObject("data")
                    val getUsername = dataObject.getString("username")
                    view.signSucceed(getUsername)
                }
            }

        })
    }

    override fun isSucceed(jsonData: String): Pair<Int, String> {
        val jsonObject = JSONObject(jsonData)
        val errorCode = jsonObject.getInt("errorCode")
        val errorMsg = jsonObject.getString("errorMsg")
        return Pair(errorCode, errorMsg)
    }
}