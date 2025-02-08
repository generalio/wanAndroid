package com.generlas.winterexam.contract

import com.generlas.winterexam.model.NavigationInfo
import okhttp3.Callback

/**
 * description ： TODO:NavigationFragment的接口
 * date : 2025/2/8 22:57
 */
interface NavigationContract {
    interface model {
        fun loadNavigationInfo(callback: Callback)
    }
    interface view {
        fun showError(message: String)
        fun initView(navigationInfo: List<NavigationInfo>)
    }
    interface presenter {
        fun getInfo()
    }
}