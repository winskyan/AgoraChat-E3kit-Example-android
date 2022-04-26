package io.agora.e3kitdemo.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.agora.e3kitdemo.R
import io.agora.e3kitdemo.base.BaseActivity
import io.agora.e3kitdemo.databinding.ActivityLoginBinding
import io.agora.e3kitdemo.login.LoginViewModel
import io.agora.e3kitdemo.net.Resource
import io.agora.e3kitdemo.utils.OnResourceParseCallback
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        initListener()
    }

    private fun initListener() {
        binding.btnLogin.setOnClickListener { loginToAgoraChat() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initData() {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.loginObservable.observe(this, Observer<Resource<Boolean>> { response ->
            this@LoginActivity.parseResource(
                response,
                object : OnResourceParseCallback<Boolean?>() {
                    override fun onSuccess(data: Boolean?) {
                        val intent = Intent(mContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onError(code: Int, message: String?) {
                        super.onError(code, message)
                        runOnUiThread {
                            btn_login.isEnabled = true
                            setErrorHint(message!!)
                        }
                    }
                })
        })
    }


    private fun loginToAgoraChat() {
        setErrorHint("")
        val agoraID: String = binding.etAgoraId.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(agoraID)) {
            setErrorHint(getString(R.string.sign_error_not_id))
            return
        }
        val nickname: String = binding.etNickname.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(nickname)) {
            setErrorHint(getString(R.string.sign_error_not_nickname))
            return
        }
        binding.logoIv.isEnabled = false
        viewModel.login(agoraID, nickname)
    }

    private fun setErrorHint(error: String) {
        binding.tvHint.text = error
        val fail = resources.getDrawable(R.drawable.failed)
        showLeftDrawable(binding.tvHint, fail)
    }

    private fun showLeftDrawable(editText: TextView, left: Drawable?) {
        val content = editText.text.toString().trim { it <= ' ' }
        editText.setCompoundDrawablesWithIntrinsicBounds(
            if (TextUtils.isEmpty(content)) null else left,
            null,
            null,
            null
        )
    }
}

