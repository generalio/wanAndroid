package com.generlas.winterexam.view.fragment

import android.annotation.SuppressLint
import android.media.RouteListingPreference
import android.media.RouteListingPreference.Item
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.repository.model.NavigationInfo
import com.generlas.winterexam.repository.model.PassageInfo
import com.generlas.winterexam.util.HttpUtil
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.adapter.NavigationContentAdapter
import com.generlas.winterexam.view.adapter.NavigationListAdapter
import com.generlas.winterexam.view.adapter.PassageAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class NavigationFragment : Fragment(), NavigationListAdapter.OnItemClickListener {

    lateinit var mainActivity: MainActivity
    var navigationInfo: List<NavigationInfo> = listOf()
    lateinit var listRecyclerView: RecyclerView
    lateinit var mTvNavigation: TextView
    lateinit var contentRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        listRecyclerView = view.findViewById(R.id.navigation_recyclerview)
        contentRecyclerView = view.findViewById(R.id.navigation_content_recyclerView)
        mTvNavigation = view.findViewById(R.id.tv_navigation)

        getInfo()
    }

    //获取导航的信息
    private fun getInfo() {
        val url = "https://www.wanandroid.com/navi/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val jsonArray = jsonObject.getAsJsonArray("data")
                val typeOf = object : TypeToken<List<NavigationInfo>>() {}.type
                mainActivity.runOnUiThread {
                    navigationInfo = gson.fromJson(jsonArray, typeOf)
                    initView()
                }
            }

        })
    }

    private fun initView() {
        listRecyclerView.layoutManager = LinearLayoutManager(mainActivity)
        listRecyclerView.adapter = NavigationListAdapter(mainActivity, navigationInfo, this)

        mTvNavigation.text = navigationInfo[0].name

        //设置流式布局
        contentRecyclerView.layoutManager = FlexboxLayoutManager(mainActivity)
        contentRecyclerView.adapter =
            NavigationContentAdapter(mainActivity, navigationInfo[0].articles)
    }

    //监听导航栏的切换
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(navigationItem: NavigationInfo) {
        mTvNavigation.text = navigationItem.name
        val adapter = NavigationContentAdapter(mainActivity, navigationItem.articles)
        contentRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}