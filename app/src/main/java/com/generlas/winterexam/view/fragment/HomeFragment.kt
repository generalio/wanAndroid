package com.generlas.winterexam.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    var passageCard: MutableList<PassageInfo> = mutableListOf()
    var finalCarouselPassage: MutableList<CarouselInfo> = mutableListOf()
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var passageAdapter: PassageAdapter
    var page : Int = 1
    lateinit var carouselAdapter: CarouselViewPager2Adapter
    lateinit var floatButton: FloatingActionButton
    lateinit var mTvCarouselTitle: TextView

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

        mTvCarouselTitle = view.findViewById(R.id.tv_carousel_text)
        floatButton = view.findViewById(R.id.float_home)
        floatButton.setOnClickListener {
            passageRecyclerView2.scrollToPosition(0)
        }
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
        if (activity != null) {
            val mainActivity = activity as MainActivity
            passageAdapter = PassageAdapter(mainActivity)
            passageRecyclerView2.layoutManager = LinearLayoutManager(mainActivity)
            passageRecyclerView2.adapter = passageAdapter
            passageAdapter.submitList(passageList)
            listenAddPassage()
        }
    }

    //监听滑倒最底部时
    private fun listenAddPassage() {
        passageRecyclerView2.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItem = layoutManager.itemCount
                val lastItem = layoutManager.findLastVisibleItemPosition()
                if(lastItem == totalItem - 1) {
                    loadMore()
                }
            }
        })
    }

    //加载更多内容
    private fun loadMore() {
        val url = "https://www.wanandroid.com/article/list/$page/json"
        page++
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
                    val newPassageCard : List<PassageInfo> = gson.fromJson(jsonData, typeOf)
                    //提交新的内容
                    passageCard.addAll(newPassageCard)
                    passageAdapter.submitList(passageCard.toList())
                }
            }
        })
    }

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
                requireActivity().runOnUiThread {
                    carouselPassage = gson.fromJson(passageData, typeOf)
                    createCarousel(carouselPassage)
                }
            }

        })
    }

    //加载轮播图适配器
    private fun createCarousel(carouselPassage: List<CarouselInfo>) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            //多添加两张，模拟最后一张到第一张
            finalCarouselPassage.add(0, carouselPassage[carouselPassage.size - 1])
            finalCarouselPassage.addAll(carouselPassage)
            finalCarouselPassage.add(carouselPassage.size + 1, carouselPassage[0])
            carouselAdapter = CarouselViewPager2Adapter(mainActivity, finalCarouselPassage.toList())
            carouselViewPager2.adapter = carouselAdapter
            carouselViewPager2.currentItem = 1
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

            //当滑动到最后一张(实际上展示为第一张的内容)时，立刻跳到第一张，滑动到第0张(展示为最后一张)时,同理
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mTvCarouselTitle.text = finalCarouselPassage[position].title
                if(position == finalCarouselPassage.size - 1) {
                    carouselViewPager2.setCurrentItem(1, false) //不带动画的跳转
                } else {
                    if(position == 0) {
                        carouselViewPager2.setCurrentItem(finalCarouselPassage.size - 2, false)
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
                val nextPassage = (carouselViewPager2.currentItem + 1) % finalCarouselPassage.size
                if(carouselViewPager2.currentItem == finalCarouselPassage.size - 1) {
                    carouselViewPager2.setCurrentItem(1, false)
                    handler.postDelayed(this, 0)
                } else {
                    carouselViewPager2.currentItem = nextPassage
                    handler.postDelayed(this, 3000)//三秒切换一次
                }
            }
        }
        handler.postDelayed(runnable, 3000)
    }
}