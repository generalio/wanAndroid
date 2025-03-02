package com.generlas.winterexam.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.generlas.winterexam.R
import com.generlas.winterexam.contract.HomeContract
import com.generlas.winterexam.model.CarouselInfo
import com.generlas.winterexam.model.HomeModel
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.presenter.HomePresenter
import com.generlas.winterexam.view.CarouselDot
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.adapter.PassageAdapter
import com.generlas.winterexam.view.adapter.CarouselViewPager2Adapter
import com.generlas.winterexam.view.adapter.HomePassageAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(), HomeContract.view {

    lateinit var carouselViewPager2: ViewPager2
    lateinit var passageRecyclerView2: RecyclerView
    var carouselPassage: List<CarouselInfo> = listOf()
    var passageCard: MutableList<PassageInfo> = mutableListOf()
    var finalCarouselPassage: MutableList<CarouselInfo> = mutableListOf()
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var passageAdapter: HomePassageAdapter
    var page : Int = 1
    lateinit var floatButton: FloatingActionButton
    lateinit var progressBar: ProgressBar
    lateinit var presenter: HomePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = HomePresenter(this, HomeModel())

        //创建轮播图
        presenter.initCarousel()

        //创建文章Card
        passageRecyclerView2 = view.findViewById(R.id.home_recyclerView)
        presenter.initPassage()

        progressBar = view.findViewById(R.id.home_progressbar)
        floatButton = view.findViewById(R.id.float_home)
        floatButton.setOnClickListener {
            passageRecyclerView2.scrollToPosition(0)
        }
    }

    override fun showError(message: String) {
        Log.d("zzx",message)
    }

    //创建文章列表
    override fun createPassageCard(passageData: List<PassageInfo>) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.runOnUiThread {
                progressBar.visibility =View.GONE
                passageCard.addAll(passageData)
                passageAdapter = HomePassageAdapter(mainActivity, finalCarouselPassage.toList())
                passageRecyclerView2.layoutManager = LinearLayoutManager(mainActivity)
                passageRecyclerView2.adapter = passageAdapter
                passageAdapter.submitList(passageData)
                listenAddPassage()
            }
        }
    }

    //加载更多文章
    override fun loadMorePassageCard(passageData: List<PassageInfo>) {
        passageCard.addAll(passageData)
        if(activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.runOnUiThread {
                passageAdapter.submitList(passageCard.toList())
            }
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
                    presenter.loadMore(page)
                    page++
                }
            }
        })
    }

    //加载轮播图适配器
    override fun createCarousel(carouselPassage: List<CarouselInfo>) {
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.runOnUiThread {
                //多添加两张，模拟最后一张到第一张
                finalCarouselPassage.add(0, carouselPassage[carouselPassage.size - 1])
                finalCarouselPassage.addAll(carouselPassage)
                finalCarouselPassage.add(carouselPassage.size + 1, carouselPassage[0])

            }
        }
    }
}