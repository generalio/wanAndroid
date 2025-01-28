package com.generlas.winterexam.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.generlas.winterexam.R
import com.generlas.winterexam.repository.model.UserInfo
import com.generlas.winterexam.util.HttpUtil
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    lateinit var mEtAccount: EditText
    lateinit var mEtPassword: EditText
    lateinit var mTilAccount: TextInputLayout
    lateinit var mTilPassword: TextInputLayout
    lateinit var mBtnLogin: Button
    lateinit var mBtnSign: Button
    lateinit var mCbRemember: CheckBox
    lateinit var mIvClose: ImageView
    lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        receiveSign()
        initEvent()
    }

    //接受sign返回的username
    private fun receiveSign() {
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val getUsername = data?.getStringExtra("getUsername").toString()
                    mEtAccount.setText(getUsername)
                }
            }
    }

    //初始化控件
    private fun initView() {
        mEtAccount = findViewById(R.id.et_login_account)
        mEtPassword = findViewById(R.id.et_login_password)
        mTilAccount = findViewById(R.id.til_login_account)
        mTilPassword = findViewById(R.id.til_login_password)
        mBtnLogin = findViewById(R.id.btn_login_login)
        mBtnSign = findViewById(R.id.btn_login_sign)
        mCbRemember = findViewById(R.id.cb_login_remember)
        mIvClose = findViewById(R.id.iv_login_close)
    }

    //检测是否点击登录/注册,对输入框的监听以及是否记住密码
    private fun initEvent() {
        initInfo()
        mEtAccount.addTextChangedListener {
            if (mEtAccount.text.toString() != "") {
                mTilAccount.setErrorEnabled(false)
            }
        }
        mEtPassword.addTextChangedListener {
            if (mEtPassword.text.toString() != "") {
                mTilPassword.setErrorEnabled(false)
            }
        }
        mBtnLogin.setOnClickListener {
            if (mEtAccount.text.toString() == "") {
                mTilAccount.setError("账户名不能为空")
            } else {
                if (mEtPassword.text.toString() == "") {
                    mTilPassword.setError("密码不能为空")
                } else {
                    login()
                }
            }
        }
        mBtnSign.setOnClickListener {
            sign()
        }
        mIvClose.setOnClickListener {
            finish()
        }
        if (mCbRemember.isChecked) {
            val username = mEtAccount.text.toString()
            val password = mEtPassword.text.toString()
            val editor = getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()
        }
    }

    //初始化账号密码
    private fun initInfo() {
        val output = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val username: String = output.getString("username", "").toString()
        val password: String = output.getString("password", "").toString()
        mEtAccount.setText(username)
        mEtPassword.setText(password)
    }

    //登陆操作
    private fun login() {
        val urlLogin: String = "https://www.wanandroid.com/user/login"
        val username: String = mEtAccount.text.toString()
        val password: String = mEtPassword.text.toString()

        val userData = mapOf("username" to username, "password" to password)
        val httpUtil = HttpUtil()
        httpUtil.Http_Post(urlLogin, userData, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("zzx", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val (errorCode, errorMsg) = checkIsSucceed(responseData.toString())
                if (errorCode != -1) {
                    //解析json数据
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(responseData).asJsonObject
                    val userData = jsonObject.getAsJsonObject("data")
                    val user = gson.fromJson(userData, UserInfo::class.java)
                    persistentReserved(username, password)
                    loginSucceed(user)
                } else {
                    loginFailed()
                }
            }
        })
    }

    //检测是否登陆成功并返回错误代码和信息
    private fun checkIsSucceed(jsonData: String): Pair<Int, String> {
        val jsonObject = JSONObject(jsonData)
        val errorCode = jsonObject.getInt("errorCode")
        val errorMsg = jsonObject.getString("errorMsg")
        return Pair(errorCode, errorMsg)
    }

    //持久化保存账号密码
    private fun persistentReserved(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("Cookies", Context.MODE_PRIVATE).edit()
        sharedPreferences.putString("username", username)
        sharedPreferences.putString("password", password)
        sharedPreferences.apply()
    }

    //登陆失败时
    private fun loginFailed() {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setTitle("登录失败！")
                setMessage("账号或密码错误，请重新登录！")
                setCancelable(false)
                setPositiveButton("确认") { dialog, which -> clearInput() }
                show()
            }
        }
    }

    //登陆成功时
    private fun loginSucceed(user: UserInfo) {
        val sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit()
        sharedPreferences.putString("username", user.username)
        sharedPreferences.putString("nickname", user.nickname)
        sharedPreferences.putInt("id", user.id)
        sharedPreferences.putInt("coinCount", user.coinCount)
        sharedPreferences.putString("email", user.email)
        sharedPreferences.apply()

        val resultIntent = Intent()
        resultIntent.putExtra("username", user.username)
        setResult(RESULT_OK, resultIntent)

        finish()

        runOnUiThread {
            Toast.makeText(this, "登录成功!", Toast.LENGTH_SHORT).show()
        }
    }

    //清除输入框的数据
    private fun clearInput() {
        mEtAccount.setText("")
        mEtPassword.setText("")
    }

    //跳转注册界面
    private fun sign() {
        val intent = Intent(this, SignActivity::class.java)
        startForResult.launch(intent)
    }

}