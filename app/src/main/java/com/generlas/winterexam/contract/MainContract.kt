package com.generlas.winterexam.contract

import okhttp3.Callback

/**
 * description ： TODO:MainActivity的接口
 * date : 2025/2/6 16:48
 */
interface MainContract {
    interface Model {
        fun autoLogin(username: String, password: String, callback: Callback)
        fun logout(callback: Callback)
    }

    interface Presenter {
        fun onLogout()
    }

    interface View {
        fun showInfo(username: String, coinCount: Int)
        fun showError(message: String)
        fun showLogout()
    }
}