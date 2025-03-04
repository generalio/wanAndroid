package com.generlas.winterexam.model

import android.content.Context
import com.generlas.winterexam.contract.NavigationContract
import okhttp3.Callback

/**
 * description ： TODO:NavigationFragment的Model
 * date : 2025/2/8 23:02
 */
class NavigationModel(private val context: Context) : NavigationContract.model {
    override fun loadNavigationInfo(callback: Callback) {
        val url = "https://www.wanandroid.com/navi/json"
        val httpUtil = HttpUtil(context)
        httpUtil.Http_Get(url, callback)
    }
}