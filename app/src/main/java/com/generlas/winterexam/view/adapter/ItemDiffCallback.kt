package com.generlas.winterexam.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.generlas.winterexam.model.PassageInfo

/**
 * description ： TODO:比较新旧数据项，实现差分刷新
 * date : 2025/2/2 14:54
 */
class ItemDiffCallback : DiffUtil.ItemCallback<PassageInfo>() {
    override fun areContentsTheSame(oldItem: PassageInfo, newItem: PassageInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: PassageInfo, newItem: PassageInfo): Boolean {
        return oldItem == newItem
    }

}