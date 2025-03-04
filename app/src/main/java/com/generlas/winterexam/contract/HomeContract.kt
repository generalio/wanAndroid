package com.generlas.winterexam.contract

import com.generlas.winterexam.model.CarouselInfo
import com.generlas.winterexam.model.PassageInfo
import okhttp3.Callback

/**
 * description ： TODO:HomeFragment的接口
 * date : 2025/2/7 22:25
 */
interface HomeContract {

    interface model {
        fun loadPassage(page: Int,callback: Callback)
        fun loadCarouselPassage(callback: Callback)
        fun checkLogin(callback: Callback)
        fun collect(id: Int,callback: Callback)
        fun unCollect(id: Int, callback: Callback)
    }

    interface view {
        fun showError(message: String)
        fun createPassageCard(passageData: List<PassageInfo>)
        fun loadMorePassageCard(passageData: List<PassageInfo>)
        fun createCarousel(carouselInfo: List<CarouselInfo>)
        fun succeedCollect()
        fun succeedUnCollect()
    }

    interface presenter {
        fun initPassage()
        fun loadMore(page: Int)
        fun initCarousel()
        fun isLogin()
        fun updateCollect()
        fun collect(id: Int)
        fun unCollect(id: Int)
    }
}