package com.generlas.winterexam.contract

import com.generlas.winterexam.model.PassageInfo
import okhttp3.Callback

/**
 * description ： TODO:SearchActivity的接口
 * date : 2025/2/9 00:23
 */
interface SearchContract {

    interface model {
        fun getSearchInfo(k: String, page: Int, callback: Callback)
    }

    interface view {
        fun loadSearchInfo(passageInfo: List<PassageInfo>)
        fun loadMorePassage(passageInfo: List<PassageInfo>)
        fun showError(message: String)
    }

    interface presenter {
        fun loadMore(k: String, page: Int)
        fun initSearch(k: String)
    }
}