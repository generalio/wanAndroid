package com.generlas.winterexam.model

/**
 * description ： TODO:个人信息
 * date : 2025/3/4 10:21
 */
data class PersonalInfo(val data: Datas, val errorCode: Int) {
    data class UserInfo(val username: String)
    data class CoinInfo(val coinCount: Int)
    data class Datas(val userInfo: UserInfo, val coinInfo: CoinInfo)
}