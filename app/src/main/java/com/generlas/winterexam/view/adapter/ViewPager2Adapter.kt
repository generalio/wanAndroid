package com.generlas.winterexam.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * description ： ViewPager2适配器
 * date : 2025/1/27 21:56
 */
class ViewPager2Adapter(fragment: FragmentActivity, private val list: MutableList<Fragment>) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

}