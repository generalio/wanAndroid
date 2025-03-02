package com.generlas.winterexam.contract

import com.generlas.winterexam.model.UserInfo
import okhttp3.Callback

/**
 * description ： TODO:LoginActivity的接口
 * date : 2025/2/6 22:15
 */
interface LoginContract {
    interface Model {
        fun login(username: String, password: String, callback: Callback)
        fun userInfoSave(user: UserInfo)
    }

    interface View {
        fun showError(message: String)
        fun loginFailed()
        fun loginSucceed(user: UserInfo)
    }

    interface Presenter {
        fun onLogin(username: String, password: String)
        fun initInfo(): Pair<String, String>
        fun passwordRemember(username: String, password: String)
    }
}