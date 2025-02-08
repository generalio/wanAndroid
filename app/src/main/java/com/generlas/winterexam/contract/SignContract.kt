package com.generlas.winterexam.contract

import okhttp3.Callback

/**
 * description ： TODO:SignActivity的接口
 * date : 2025/2/7 21:02
 */
interface SignContract {

    interface model {
        fun sign(username: String, password: String, rePassword: String, callback: Callback)
    }

    interface view {
        fun showError(message: String)
        fun signFailed(errorMsg: String)
        fun signSucceed(username: String)
    }

    interface Presenter {
        fun onSign(username: String, password: String, rePassword: String)
        fun isSucceed(jsonData: String) : Pair<Int, String>
    }
}