package com.generlas.winterexam.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.repository.model.PassageInfo
import com.generlas.winterexam.repository.model.PublicAuthorInfo
import com.generlas.winterexam.util.HttpUtil
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.adapter.PassageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type

class PublicFragment : Fragment() {

    lateinit var tabLayout: TabLayout
    lateinit var recyclerView: RecyclerView
    lateinit var mainActivity: MainActivity
    var authorInfo: List<PublicAuthorInfo> = listOf()
    var passageInfo: List<PassageInfo> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_public, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        tabLayout = view.findViewById(R.id.tab_public)
        recyclerView = view.findViewById(R.id.public_recyclerView)

        getAuthorInfo()

    }

    //加载tablayout布局以及对应的文章列表
    private fun setTab() {

        //加载公众号列表
        for (author in authorInfo) {
            tabLayout.addTab(tabLayout.newTab().setText(author.name))
        }

        setPassageList(authorInfo[0].id)

        //对tab选择的监听
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    setPassageList(authorInfo[tab.position].id)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    //加载对应作者的文章列表
    @SuppressLint("NotifyDataSetChanged")
    private fun setPassageList(id: Int) {
        val url = "https://wanandroid.com/wxarticle/list/$id/1/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val jsonObjectData = jsonObject.getAsJsonObject("data")
                val jsonArray = jsonObjectData.getAsJsonArray("datas")
                val typeOf = object : TypeToken<List<PassageInfo>>() {}.type
                mainActivity.runOnUiThread {
                    passageInfo = gson.fromJson(jsonArray, typeOf)
                    val adapter = PassageAdapter(mainActivity, passageInfo)
                    recyclerView.layoutManager = LinearLayoutManager(mainActivity)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

        })
    }

    //获取公众号作者信息
    private fun getAuthorInfo() {
        val url = "https://wanandroid.com/wxarticle/chapters/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val authorArray = jsonObject.getAsJsonArray("data")
                val typeOf = object : TypeToken<List<PublicAuthorInfo>>() {}.type
                mainActivity.runOnUiThread {
                    authorInfo = gson.fromJson(authorArray, typeOf)
                    setTab()
                }
            }

        })
    }

}