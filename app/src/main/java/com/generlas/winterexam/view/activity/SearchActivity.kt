package com.generlas.winterexam.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.contract.SearchContract
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.model.SearchModel
import com.generlas.winterexam.presenter.SearchPresenter
import com.generlas.winterexam.view.adapter.PassageAdapter

class SearchActivity : AppCompatActivity() , SearchContract.view {

    lateinit var mIvBack: ImageView
    lateinit var mEtSearch: EditText
    lateinit var mIvSearch: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var presenter: SearchPresenter
    lateinit var adapter: PassageAdapter
    var passageList: MutableList<PassageInfo> = mutableListOf()
    var page = 1
    var key = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        presenter = SearchPresenter(this, SearchModel())

        mIvBack = findViewById(R.id.iv_search_back)
        mIvSearch = findViewById(R.id.iv_search_search)
        mEtSearch = findViewById(R.id.et_search_search)
        recyclerView = findViewById(R.id.search_recyclerview)

        mIvBack.setOnClickListener {
            finish()
        }

        initView()
    }

    private fun initView() {
        mIvSearch.setOnClickListener {
            key = mEtSearch.text.toString()
            page = 1
            presenter.initSearch(key)
        }
    }

    //加载搜索信息
    override fun loadSearchInfo(passageInfo: List<PassageInfo>) {
        runOnUiThread {
            passageList = passageInfo.toMutableList()
            adapter = PassageAdapter(this)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            adapter.submitList(passageInfo)
            listenAdd()
        }
    }

    override fun loadMorePassage(passageInfo: List<PassageInfo>) {
        runOnUiThread {
            passageList.addAll(passageInfo)
            adapter.submitList(passageList.toList())
        }
    }

    private fun listenAdd() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItem = layoutManager.itemCount
                val lastItem = layoutManager.findLastVisibleItemPosition()
                if(lastItem == totalItem - 1) {
                    presenter.loadMore(key, page)
                    page++
                }
            }
        })
    }

    override fun showError(message: String) {
        Log.d("zzx",message)
    }
}