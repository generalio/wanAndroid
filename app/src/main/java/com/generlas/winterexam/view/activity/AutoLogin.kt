package com.generlas.winterexam.view.activity

import com.generlas.winterexam.model.PersonalInfo

/**
 * description ： TODO:自动登录成功的回调
 * date : 2025/3/4 09:58
 */
interface AutoLogin {
    fun isLoginSuccess(personalInfo: PersonalInfo)
}