package com.generlas.winterexam.presenter

import com.generlas.winterexam.contract.NavigationContract
import com.generlas.winterexam.model.NavigationInfo
import com.generlas.winterexam.model.NavigationModel
import com.generlas.winterexam.view.fragment.NavigationFragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * description ： TODO:NavigationFragment的Presenter
 * date : 2025/2/8 23:03
 */
class NavigationPresenter(private val view: NavigationFragment, private val model: NavigationModel) : NavigationContract.presenter {

    //获取轮播图信息
    override fun getInfo() {
        model.loadNavigationInfo(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val jsonArray = jsonObject.getAsJsonArray("data")
                val typeOf = object : TypeToken<List<NavigationInfo>>() {}.type
                val navigationInfo: List<NavigationInfo> = gson.fromJson(jsonArray, typeOf)
                view.initView(navigationInfo)
            }

        })
    }
}