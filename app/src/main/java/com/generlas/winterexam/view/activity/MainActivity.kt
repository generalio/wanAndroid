package com.generlas.winterexam.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.generlas.winterexam.R
import com.generlas.winterexam.contract.MainContract
import com.generlas.winterexam.model.UserInfo
import com.generlas.winterexam.model.HttpUtil
import com.generlas.winterexam.model.MainModel
import com.generlas.winterexam.presenter.MainPresenter
import com.generlas.winterexam.view.adapter.ViewPager2Adapter
import com.generlas.winterexam.view.fragment.HomeFragment
import com.generlas.winterexam.view.fragment.NavigationFragment
import com.generlas.winterexam.view.fragment.PublicFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class MainActivity : AppCompatActivity(), MainContract.View {
    lateinit var presenter: MainContract.Presenter
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this, MainModel(this))

        setNavigationColumn()
        setBottomNavigation()

        // 注册 ActivityResultLauncher
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) { //如果接收到LoginActivity返回的数据
                    val data: Intent? = result.data
                    val getUsername = data?.getStringExtra("username").toString()
                    presenter.onLogin()
                }
            }
        setButton()
        presenter.onLogin()
    }

    override fun showInfo(username: String, coinCount: Int) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val headerUsername: TextView = headerView.findViewById(R.id.tv_head_username)
        val headerButton: Button = headerView.findViewById(R.id.btn_head_login)
        val headerCollectCoins: TextView = headerView.findViewById(R.id.tv_head_collectCoins)
        headerUsername.setText(username)
        if (coinCount == -1) {
            headerCollectCoins.setText("积分:--")
        } else {
            headerCollectCoins.setText("积分:$coinCount")
        }
        //更改登录按钮名称
        if (headerButton.text.toString() == "点击登录") {
            headerButton.setText("退出登录")
        } else {
            if (headerButton.text.toString() == "退出登录") {
                headerButton.setText("点击登录")
                headerCollectCoins.setText("积分:--")
            }
        }
    }

    override fun showLogout() {
        Toast.makeText(this, "已退出登录!", Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Log.d("zzx", message)
    }

    //对toolbar+drawerlayout的基本设置
    private fun setNavigationColumn() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "玩Android"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        fun closeDrawer() {
            drawerLayout.closeDrawer(findViewById(R.id.nav_view))
        }

        //对抽屉懒里面的列表进行点击监听
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }

            }
            closeDrawer()
            true
        }
        toggle = object :
            ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }
        toggle.syncState()
        drawerLayout.addDrawerListener(toggle)
    }

    // 处理toolbar上抽屉开关等图标的点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //前往登录/登出
    private fun setButton() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val mBtnLogin: Button = headerView.findViewById(R.id.btn_head_login)
        mBtnLogin.setOnClickListener {
            if (mBtnLogin.text.toString() == "点击登录") {
                val intent = Intent(this, LoginActivity::class.java)
                startForResult.launch(intent)
            }
            if (mBtnLogin.text.toString() == "退出登录") {
                AlertDialog.Builder(this).apply {
                    setMessage("确认退出登录？")
                    setCancelable(true)
                    setPositiveButton("确认") { dialog, which -> presenter.onLogout() }
                    setNegativeButton("取消") { dialog, which -> }
                    show()
                }
            }
        }
    }

    //设置底部导航按钮
    private fun setBottomNavigation() {
        //绑定视图
        val mNavBottom: BottomNavigationView = findViewById(R.id.homepage_navigation_bottom)
        val mBottomViewpager2: ViewPager2 = findViewById(R.id.bottom_navigation_viewpage2)

        mNavBottom.itemIconTintList = null
        //将fragment添加进适配器
        val fragmentList: MutableList<Fragment> = ArrayList()
        fragmentList.add(HomeFragment())
        fragmentList.add(PublicFragment())
        fragmentList.add(NavigationFragment())
        mBottomViewpager2.adapter = ViewPager2Adapter(this, fragmentList)

        mBottomViewpager2.isUserInputEnabled = false

        //fragment切换时底部导航跟着切换
        mBottomViewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mNavBottom.menu.getItem(position).isChecked = true
            }
        })

        //底部导航切换时fragment跟着切换
        mNavBottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    mBottomViewpager2.currentItem = 0
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_public -> {
                    mBottomViewpager2.currentItem = 1
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_navigation -> {
                    mBottomViewpager2.currentItem = 2
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}