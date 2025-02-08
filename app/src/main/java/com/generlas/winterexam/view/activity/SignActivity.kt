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
import com.generlas.winterexam.contract.SignContract
import com.generlas.winterexam.model.HttpUtil
import com.generlas.winterexam.model.SignModel
import com.generlas.winterexam.presenter.SignPresenter
import com.google.android.material.textfield.TextInputLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SignActivity : AppCompatActivity(), SignContract.view {

    lateinit var mEtUsername: EditText
    lateinit var mEtPassword: EditText
    lateinit var mEtRePassword: EditText
    lateinit var mTilUsername: TextInputLayout
    lateinit var mTilPassword: TextInputLayout
    lateinit var mTilRePassword: TextInputLayout
    lateinit var mIvClose: ImageView
    lateinit var mBtnSign: Button
    lateinit var presenter: SignPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        presenter = SignPresenter(this, SignModel(this))

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
                        presenter.onSign(username, password, rePassword)
                    }
                }
            }
        }
    }

    override fun signFailed(errorMsg: String) {
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

    override fun signSucceed(username: String) {
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

    override fun showError(message: String) {
        Log.d("zzx",message)
    }
}