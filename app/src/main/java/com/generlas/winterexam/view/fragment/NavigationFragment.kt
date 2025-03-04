package com.generlas.winterexam.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.generlas.winterexam.R
import com.generlas.winterexam.contract.NavigationContract
import com.generlas.winterexam.model.NavigationInfo
import com.generlas.winterexam.model.HttpUtil
import com.generlas.winterexam.model.NavigationModel
import com.generlas.winterexam.presenter.NavigationPresenter
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.adapter.NavigationContentAdapter
import com.generlas.winterexam.view.adapter.NavigationListAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class NavigationFragment : Fragment(), NavigationListAdapter.OnItemClickListener,
    NavigationContract.view {

    lateinit var mainActivity: MainActivity
    lateinit var listRecyclerView: RecyclerView
    lateinit var mTvNavigation: TextView
    lateinit var contentRecyclerView: RecyclerView
    lateinit var floatButton: FloatingActionButton
    lateinit var progressBar: ProgressBar
    lateinit var presenter: NavigationPresenter

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
        progressBar = view.findViewById(R.id.navigation_progressbar)
        listRecyclerView = view.findViewById(R.id.navigation_recyclerview)
        contentRecyclerView = view.findViewById(R.id.navigation_content_recyclerView)
        mTvNavigation = view.findViewById(R.id.tv_navigation)
        floatButton = view.findViewById(R.id.float_navigation)
        presenter = NavigationPresenter(this, NavigationModel(mainActivity))

        floatButton.setOnClickListener {
            contentRecyclerView.scrollToPosition(0)
        }

        presenter.getInfo()
    }

    //初始化导航信息
    override fun initView(navigationInfo: List<NavigationInfo>) {
        mainActivity.runOnUiThread {
            progressBar.visibility = View.GONE
            listRecyclerView.layoutManager = LinearLayoutManager(mainActivity)
            listRecyclerView.adapter = NavigationListAdapter(mainActivity, navigationInfo, this)

            mTvNavigation.text = navigationInfo[0].name

            //设置流式布局
            contentRecyclerView.layoutManager = FlexboxLayoutManager(mainActivity)
            contentRecyclerView.adapter =
                NavigationContentAdapter(mainActivity, navigationInfo[0].articles)
        }
    }

    //监听导航栏的切换
    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(navigationItem: NavigationInfo) {
        mTvNavigation.text = navigationItem.name
        val adapter = NavigationContentAdapter(mainActivity, navigationItem.articles)
        contentRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun showError(message: String) {
        Log.d("zzx", message);
    }
}