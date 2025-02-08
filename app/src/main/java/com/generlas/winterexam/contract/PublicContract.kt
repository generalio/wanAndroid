package com.generlas.winterexam.contract

import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.model.PublicAuthorInfo
import okhttp3.Callback

/**
 * description ： TODO:PublicFragment的接口
 * date : 2025/2/8 22:14
 */
interface PublicContract {
    interface model{
        fun loadPassage(id: Int, page: Int, callback: Callback)
        fun loadAuthor(callback: Callback)
    }
    interface view {
        fun showError(message: String)
        fun createPassageList(passageData: List<PassageInfo>, id: Int)
        fun loadMorePassage(passageData: List<PassageInfo>)
        fun setAuthorInfo(authorInfo: List<PublicAuthorInfo>)
    }
    interface presenter {
        fun initAuthor()
        fun initPassage(id: Int)
        fun loadMore(id: Int, page: Int)
    }
}