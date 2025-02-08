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
import com.generlas.winterexam.contract.LoginContract
import com.generlas.winterexam.model.UserInfo
import com.generlas.winterexam.model.HttpUtil
import com.generlas.winterexam.model.LoginModel
import com.generlas.winterexam.model.MainModel
import com.generlas.winterexam.presenter.LoginPresenter
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() , LoginContract.View {

    lateinit var mEtAccount: EditText
    lateinit var mEtPassword: EditText
    lateinit var mTilAccount: TextInputLayout
    lateinit var mTilPassword: TextInputLayout
    lateinit var mBtnLogin: Button
    lateinit var mBtnSign: Button
    lateinit var mCbRemember: CheckBox
    lateinit var mIvClose: ImageView
    lateinit var startForResult: ActivityResultLauncher<Intent>
    lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this, LoginModel(this))

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
                    presenter.onLogin(mEtAccount.text.toString(), mEtPassword.text.toString())
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
            presenter.passwordRemember(username, password)
        }
    }

    //初始化账号密码
    private fun initInfo() {
        val (username, password) = presenter.initInfo()
        mEtAccount.setText(username)
        mEtPassword.setText(password)
    }

    override fun showError(message: String) {
        Log.d("zzx",message)
    }

    //登陆失败时
    override fun loginFailed() {
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
   override fun loginSucceed(user: UserInfo) {
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