package com.generlas.winterexam.presenter

import com.generlas.winterexam.view.activity.SearchActivity
import com.generlas.winterexam.contract.SearchContract
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.model.SearchModel
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * description ： TODO:SearchActivity的Presenter
 * date : 2025/2/9 00:24
 */
class SearchPresenter(val view: SearchActivity, val model: SearchModel) : SearchContract.presenter {
    override fun loadMore(k: String, page: Int) {
        model.getSearchInfo(k, page, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val json = JsonParser.parseString(responseData).asJsonObject
                val jsonObject = json.getAsJsonObject("data")
                val jsonData = jsonObject.getAsJsonArray("datas")
                val typeOf = object : TypeToken<List<PassageInfo>>() {}.type
                val passageData : List<PassageInfo> = gson.fromJson(jsonData, typeOf)
                view.loadMorePassage(passageData)
            }

        })
    }

    override fun initSearch(k: String) {
        model.getSearchInfo(k, 0, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val json = JsonParser.parseString(responseData).asJsonObject
                val jsonObject = json.getAsJsonObject("data")
                val jsonData = jsonObject.getAsJsonArray("datas")
                val typeOf = object : TypeToken<List<PassageInfo>>() {}.type
                val passageData : List<PassageInfo> = gson.fromJson(jsonData, typeOf)
                view.loadSearchInfo(passageData)
            }

        })
    }
}