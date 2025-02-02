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
import com.generlas.winterexam.repository.model.UserInfo
import com.generlas.winterexam.util.HttpUtil
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


class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavigationColumn()
        setBottomNavigation()
        CookiesRequest()

        // 注册 ActivityResultLauncher
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) { //如果接收到LoginActivity返回的数据
                    val data: Intent? = result.data
                    val getUsername = data?.getStringExtra("username").toString()
                    setUsername(getUsername)
                }
            }
        setButton()
    }

    //为侧边栏顶部设置登录的用户名
    @SuppressLint("SetTextI18n")
    private fun setUsername(username: String) {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val headerUsername: TextView = headerView.findViewById(R.id.tv_head_username)
        val headerButton: Button = headerView.findViewById(R.id.btn_head_login)
        val headerCollectCoins: TextView = headerView.findViewById(R.id.tv_head_collectCoins)
        headerUsername.setText(username)
        val sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val coinCount = sharedPreferences.getInt("coinCount", -1)
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
                    setPositiveButton("确认") { dialog, which -> logOut() }
                    setNegativeButton("取消") { dialog, which -> }
                    show()
                }
            }
        }
    }

    //模拟Cookie请求，在退出登录后会自动清楚Cookie中的数据
    private fun CookiesRequest() {
        val sharedPreferences = getSharedPreferences("Cookies", Context.MODE_PRIVATE)
        val presistentUsername = sharedPreferences.getString("username", "").toString()
        val presistentPassword = sharedPreferences.getString("password", "").toString()
        val urlLogin: String = "https://www.wanandroid.com/user/login"
        if (presistentUsername != "" && presistentPassword != "") {
            val userData = mapOf("username" to presistentUsername, "password" to presistentPassword)
            val httpUtil = HttpUtil()
            httpUtil.Http_Post(urlLogin, userData, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("zzx", e.message.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body?.string()
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(responseData).asJsonObject
                    val userData = jsonObject.getAsJsonObject("data")
                    val user = gson.fromJson(userData, UserInfo::class.java)
                    initAutoLogin(user)
                }
            })
        }
    }

    //自动登录
    private fun initAutoLogin(user: UserInfo) {
        val sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit()
        sharedPreferences.putString("username", user.username)
        sharedPreferences.putString("nickname", user.nickname)
        sharedPreferences.putInt("id", user.id)
        sharedPreferences.putInt("coinCount", user.coinCount)
        sharedPreferences.putString("email", user.email)
        runOnUiThread {
            setUsername(user.username)
        }
        sharedPreferences.apply()
    }

    //处理退出登录
    private fun logOut() {
        val url = "https://www.wanandroid.com/user/logout/json"
        val httpUtil = HttpUtil()
        httpUtil.Http_Get(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val jsonObject = JSONObject(responseData)
                val errorCode = jsonObject.getInt("errorCode")
                if (errorCode == 0) {
                    clearUserInfo()
                    logOutSucceed()
                }
            }
        })
    }

    //成功退出登录
    private fun logOutSucceed() {
        runOnUiThread {
            Toast.makeText(this, "已退出登录!", Toast.LENGTH_SHORT).show()
            setUsername("未登录")
        }
    }

    //退出登录时清理用户信息
    private fun clearUserInfo() {
        runOnUiThread {
            val sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit()
            sharedPreferences.apply {
                remove("username")
                remove("nickname")
                remove("id")
                remove("email")
                remove("coinCount")
            }
            sharedPreferences.apply()
            val Cookies = getSharedPreferences("Cookies", Context.MODE_PRIVATE).edit()
            Cookies.apply {
                remove("username")
                remove("password")
            }
            Cookies.apply()
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