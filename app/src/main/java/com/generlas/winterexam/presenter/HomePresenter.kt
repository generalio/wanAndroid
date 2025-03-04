package com.generlas.winterexam.presenter

import android.util.Log
import com.generlas.winterexam.contract.HomeContract
import com.generlas.winterexam.model.CarouselInfo
import com.generlas.winterexam.model.HomeModel
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.model.PersonalInfo
import com.generlas.winterexam.view.fragment.HomeFragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * description ： TODO:HomeFragment的Presenter
 * date : 2025/2/7 23:22
 */
class HomePresenter(private val view: HomeFragment, private val model: HomeModel) : HomeContract.presenter {

    //初始化文章数据
    override fun initPassage() {
        model.loadPassage(0, object : Callback {
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
                view.createPassageCard(passageData)
            }

        })
    }

    //加载更多文章
    override fun loadMore(page: Int) {
        model.loadPassage(page,object : Callback {
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
                val newPassageCard : List<PassageInfo> = gson.fromJson(jsonData, typeOf)
                view.loadMorePassageCard(newPassageCard)
            }

        })
    }

    //初始化轮播图
    override fun initCarousel() {
        model.loadCarouselPassage(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val passageData = jsonObject.getAsJsonArray("data")
                val typeOf = object : TypeToken<List<CarouselInfo>>() {}.type
                val carouselData : List<CarouselInfo> = gson.fromJson(passageData, typeOf)
                view.createCarousel(carouselData)
            }

        })
    }

    override fun isLogin() {
        model.checkLogin(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.showError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JSONObject(responseData)
                val errorCode : Int = jsonObject.getInt("errorCode")
                if(errorCode == 0) {
                    val personalData : PersonalInfo = gson.fromJson(responseData, PersonalInfo::class.java)
                    view.loginSucceed(personalData)
                }
            }

        })
    }

    override fun updateCollect() {
        model.loadPassage(0, object : Callback {
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
                view.updatePassageCard(passageData)
            }

        })
    }

}