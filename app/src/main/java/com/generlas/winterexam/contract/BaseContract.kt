package com.generlas.winterexam.contract

/**
 * description ： TODO:判断进行时Activity/Fragment是否为空
 * date : 2025/2/8 23:18
 */
interface BaseContract {
    interface presenter {
        fun bindingView()
        fun noBindingView()
    }
}