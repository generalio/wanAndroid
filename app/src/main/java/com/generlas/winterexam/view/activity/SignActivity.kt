package com.generlas.winterexam.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.generlas.winterexam.R
import com.generlas.winterexam.util.HttpUtil
import com.google.android.material.textfield.TextInputLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SignActivity : AppCompatActivity() {

    lateinit var mEtUsername: EditText
    lateinit var mEtPassword: EditText
    lateinit var mEtRePassword: EditText
    lateinit var mTilUsername: TextInputLayout
    lateinit var mTilPassword: TextInputLayout
    lateinit var mTilRePassword: TextInputLayout
    lateinit var mIvClose: ImageView
    lateinit var mBtnSign: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        initView()
        initEvent()
    }

    private fun initView() {
        mEtUsername = findViewById(R.id.et_sign_account)
        mEtPassword = findViewById(R.id.et_sign_password)
        mEtRePassword = findViewById(R.id.et_sign_rePassword)
        mIvClose = findViewById(R.id.iv_sign_close)
        mTilUsername = findViewById(R.id.til_sign_account)
        mTilPassword = findViewById(R.id.til_sign_password)
        mTilRePassword = findViewById(R.id.til_sign_rePassword)
        mBtnSign = findViewById(R.id.btn_sign_sign)
    }

    private fun initEvent() {
        mIvClose.setOnClickListener {
            finish()
        }
        mEtUsername.addTextChangedListener {
            mTilUsername.isErrorEnabled = false
        }
        mEtPassword.addTextChangedListener {
            mTilPassword.isErrorEnabled = false
        }
        mEtRePassword.addTextChangedListener {
            mTilRePassword.isErrorEnabled = false
        }
        mBtnSign.setOnClickListener {
            sign()
        }
    }

    private fun sign() {
        val username = mEtUsername.text.toString()
        val password = mEtPassword.text.toString()
        val rePassword = mEtRePassword.text.toString()
        if (username == "") {
            mTilUsername.setError("用户名不能为空!")
        } else {
            if (password == "") {
                mTilPassword.setError("密码不能为空!")
            } else {
                if (rePassword == "") {
                    mTilRePassword.setError("请再次输入密码!")
                } else {
                    if (password != rePassword) {
                        mTilRePassword.setError("两次密码输入不相同!")
                    } else {
                        signRequest(username, password, rePassword)
                    }
                }
            }
        }
    }

    private fun signRequest(username: String, password: String, rePassword: String) {
        val url = "https://www.wanandroid.com/user/register"
        val signData =
            mapOf("username" to username, "password" to password, "repassword" to rePassword)
        val httpUtil = HttpUtil()
        httpUtil.Http_Post(url, signData, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("zzx", e.message.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string().toString()
                val (errorCode, errorMsg) = isSucceed(responseData)
                if (errorCode == -1) {
                    signFailed(errorMsg)
                } else {
                    val jsonObject = JSONObject(responseData)
                    val dataObject = jsonObject.getJSONObject("data")
                    val getUsername = dataObject.getString("username")
                    signSucceed(getUsername)
                }
            }
        })
    }

    private fun isSucceed(jsonData: String): Pair<Int, String> {
        val jsonObject = JSONObject(jsonData)
        val errorCode = jsonObject.getInt("errorCode")
        val errorMsg = jsonObject.getString("errorMsg")
        return Pair(errorCode, errorMsg)
    }

    private fun signFailed(errorMsg: String) {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setTitle("注册失败!")
                setMessage("$errorMsg 请重新注册")
                setCancelable(false)
                setPositiveButton("确认") { dialog, which -> clearEdit() }
                show()
            }
        }
    }

    private fun signSucceed(username: String) {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setTitle("注册成功")
                setMessage("注册成功,请返回登录!")
                setCancelable(false)
                setPositiveButton("确认") { dialog, which -> returnLogin(username) }
                show()
            }
        }
    }

    private fun clearEdit() {
        mEtUsername.setText("")
        mEtPassword.setText("")
        mEtRePassword.setText("")
    }

    private fun returnLogin(username: String) {
        val result = Intent()
        result.putExtra("getUsername", username)
        setResult(RESULT_OK, result)
        finish()
    }
}