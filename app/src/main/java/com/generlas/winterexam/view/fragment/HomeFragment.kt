package com.generlas.winterexam.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.generlas.winterexam.R
import com.generlas.winterexam.repository.model.CarouselInfo
import com.generlas.winterexam.repository.model.PassageInfo
import com.generlas.winterexam.util.HttpUtil
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.adapter.PassageAdapter
import com.generlas.winterexam.view.activity.WebViewActivity
import com.generlas.winterexam.view.adapter.CarouselViewPager2Adapter
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class HomeFragment : Fragment() {

    lateinit var carouselViewPager2: ViewPager2
    lateinit var passageRecyclerView2: RecyclerView
    var carouselPassage: List<CarouselInfo> = listOf()
    var passageCard: List<PassageInfo> = listOf()
    lateinit var handler: Handler
    lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //创建轮播图
        carouselViewPager2 = view.findViewById(R.id.home_viewpager2)
        initCarouselPassage()

        //创建文章Card
        passageRecyclerView2 = view.findViewById(R.id.home_recyclerView)
        initPassageCard()

    }

    //初始化文章列表数据
    private fun initPassageCard() {

        val url = "https://www.wanandroid.com/article/list/0/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val json = JsonParser.parseString(responseData).asJsonObject
                val jsonObject = json.getAsJsonObject("data")
                val jsonData = jsonObject.getAsJsonArray("datas")
                val typeOf = object : TypeToken<List<PassageInfo>>() {}.type
                requireActivity().runOnUiThread {
                    passageCard = gson.fromJson(jsonData, typeOf)
                    createPassageCard(passageCard)
                }
            }
        })
    }

    //创建文章列表
    private fun createPassageCard(passageList: List<PassageInfo>) {
        Log.d("zzx", passageList[0].title);
        if (activity != null) {
            val mainActivity = activity as MainActivity
            val passageAdapter = PassageAdapter(mainActivity, passageList)
            passageRecyclerView2.layoutManager = LinearLayoutManager(mainActivity)
            passageRecyclerView2.adapter = passageAdapter
        }
    }

    /*override fun OnItemClick(position: Int) {
        if(activity != null) {
            val mainActivity = activity as MainActivity
            val intent = Intent(mainActivity, WebViewActivity::class.java)
            intent.putExtra("url", passageCard[position].link)
            startActivity(intent)
        }
    }*/

    //获取轮播图的信息
    private fun initCarouselPassage() {
        val url = "https://www.wanandroid.com/banner/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val gson = Gson()
                val jsonObject = JsonParser.parseString(responseData).asJsonObject
                val passageData = jsonObject.getAsJsonArray("data")
                val typeOf = object : TypeToken<List<CarouselInfo>>() {}.type
                carouselPassage = gson.fromJson(passageData, typeOf)
                requireActivity().runOnUiThread {
                    createCarousel(carouselPassage)
                }
            }

        })
    }

    //加载轮播图适配器
    private fun createCarousel(carouselPassage: List<CarouselInfo>) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            val carouselAdapter = CarouselViewPager2Adapter(mainActivity, carouselPassage)
            carouselViewPager2.adapter = carouselAdapter
            autoCarousel()
            handCarousel()
        }
    }

    //手动滑动时暂停自动滑动
    private fun handCarousel() {
        carouselViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(runnable)
                } else {
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        handler.removeCallbacks(runnable) //需把之前的线程关闭掉，否则线程越来越多，自动切换速度越快
                        handler.postDelayed(runnable, 3000)
                    }
                }
            }
        })
    }

    //自动轮播
    private fun autoCarousel() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val nextPassage = (carouselViewPager2.currentItem + 1) % carouselPassage.size
                carouselViewPager2.currentItem = nextPassage
                handler.postDelayed(this, 3000)//三秒切换一次
            }
        }
        handler.postDelayed(runnable, 3000)
    }

    //fragment销毁时释放资源
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}