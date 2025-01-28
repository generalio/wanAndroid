package com.generlas.winterexam.view.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.generlas.winterexam.R

class WebViewActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        setToolbar()
        setWebView()
    }

    //设置网页
    private fun setWebView() {
        webView = findViewById(R.id.webView)
        val url = intent.getStringExtra("url").toString()
        Open_WebView(webView, url)
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun Open_WebView(webView: WebView, url: String) {
        webView.settings.javaScriptEnabled = true

        //获取webViewClient实例，并重写当页面加载成功时获取title并设置
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                toolbar.setTitle(webView.title)
            }
        }
        webView.loadUrl(url)
    }

    //设置标题栏
    private fun setToolbar() {
        toolbar = findViewById(R.id.webView_toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }

    //加载关闭按钮资源
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar_about, menu)
        return true
    }

    //为关闭按钮设置点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_about_close -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}