package com.generlas.winterexam.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.contract.PublicContract
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.model.PublicAuthorInfo
import com.generlas.winterexam.model.PublicModel
import com.generlas.winterexam.presenter.PublicPresenter
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.adapter.PassageAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class PublicFragment : Fragment() , PublicContract.view {

    lateinit var tabLayout: TabLayout
    lateinit var recyclerView: RecyclerView
    lateinit var mainActivity: MainActivity
    lateinit var adapter: PassageAdapter
    var page: Int = 1
    var passageInfo: MutableList<PassageInfo> = mutableListOf()
    lateinit var floatButton: FloatingActionButton
    lateinit var progressBar: ProgressBar
    lateinit var presenter: PublicPresenter

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

        presenter = PublicPresenter(this, PublicModel(mainActivity))

        progressBar = view.findViewById(R.id.public_progressbar)
        tabLayout = view.findViewById(R.id.tab_public)
        recyclerView = view.findViewById(R.id.public_recyclerView)
        floatButton = view.findViewById(R.id.float_public)

        floatButton.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        presenter.initAuthor()

    }

    //加载tablayout布局以及对应的文章列表
    override fun setAuthorInfo(authorInfo: List<PublicAuthorInfo>) {

        mainActivity.runOnUiThread {
            //加载公众号列表
            for (author in authorInfo) {
                tabLayout.addTab(tabLayout.newTab().setText(author.name))
            }

            presenter.initPassage(authorInfo[0].id)

            //对tab选择的监听
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null) {
                        adapter.submitList(listOf())
                        progressBar.visibility = View.VISIBLE
                        presenter.initPassage(authorInfo[tab.position].id)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })
        }
    }

    //加载对应作者的文章列表
    @SuppressLint("NotifyDataSetChanged")
    override fun createPassageList(passageData: List<PassageInfo>,id: Int) {
        mainActivity.runOnUiThread {
            page = 1
            passageInfo = passageData.toMutableList()
            progressBar.visibility = View.GONE
            adapter = PassageAdapter(mainActivity)
            recyclerView.layoutManager = LinearLayoutManager(mainActivity)
            recyclerView.adapter = adapter
            adapter.submitList(passageData)
            listenAddPassage(id)
        }
    }

    //监听滑倒最底部
    private fun listenAddPassage(id: Int) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItem = layoutManager.itemCount
                val lastItem = layoutManager.findLastVisibleItemPosition()
                if(lastItem == totalItem - 1) {
                    presenter.loadMore(id, page)
                    page++
                }
            }
        })
    }

    //加载新的内容
    override fun loadMorePassage(passageData: List<PassageInfo>) {
        mainActivity.runOnUiThread {
            //加载新的内容
            passageInfo.addAll(passageData)
            adapter.submitList(passageInfo.toList())
        }
    }

    override fun showError(message: String) {
        Log.d("zzx",message)
    }

}