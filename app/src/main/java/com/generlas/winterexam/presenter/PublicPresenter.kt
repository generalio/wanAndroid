package com.generlas.winterexam.presenter

import android.util.Log
import com.generlas.winterexam.contract.PublicContract
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.model.PublicAuthorInfo
import com.generlas.winterexam.model.PublicModel
import com.generlas.winterexam.view.fragment.PublicFragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * description ： TODO:类的作用
 * date : 2025/2/8 22:23
 */
class PublicPresenter(val view: PublicFragment, val model: PublicModel) : PublicContract.presenter {

    //初始化作者信息
    override fun initAuthor() {
        model.loadAuthor(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val authorArray = jsonObject.getAsJsonArray("data")
                val typeOf = object : TypeToken<List<PublicAuthorInfo>>() {}.type
                val authorInfo : List<PublicAuthorInfo> = gson.fromJson(authorArray, typeOf)
                view.setAuthorInfo(authorInfo)
            }

        })
    }

    //初始化文章
    override fun initPassage(id: Int) {
        model.loadPassage(id, 0, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val jsonObjectData = jsonObject.getAsJsonObject("data")
                val jsonArray = jsonObjectData.getAsJsonArray("datas")
                val typeOf = object : TypeToken<List<PassageInfo>>() {}.type
                val passageList: List<PassageInfo> = gson.fromJson(jsonArray, typeOf)
                view.createPassageList(passageList, id)
            }

        })
    }

    //加载更多文章
    override fun loadMore(id: Int, page: Int) {
        model.loadPassage(id, page, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val jsonObjectData = jsonObject.getAsJsonObject("data")
                val jsonArray = jsonObjectData.getAsJsonArray("datas")
                val typeOf = object : TypeToken<List<PassageInfo>>() {}.type
                val passageList : List<PassageInfo> = gson.fromJson(jsonArray, typeOf)
                view.loadMorePassage(passageList)
            }

        })
    }
}