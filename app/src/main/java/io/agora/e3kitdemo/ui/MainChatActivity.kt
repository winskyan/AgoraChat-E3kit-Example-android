package io.agora.e3kitdemo.ui

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.agora.chat.ChatClient
import io.agora.e3kitdemo.DemoHelper
import io.agora.e3kitdemo.R
import io.agora.e3kitdemo.base.BaseActivity
import io.agora.e3kitdemo.databinding.ActivityChatMainBinding
import io.agora.e3kitdemo.utils.EaseThreadManager
import io.agora.e3kitdemo.utils.OnResourceParseCallback
import io.agora.e3kitdemo.login.LoginViewModel
import io.agora.e3kitdemo.net.Resource
import kotlinx.android.synthetic.main.activity_login.*


class MainChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatMainBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initData()
        initListener();
    }

    private fun initData() {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.loginObservable.observe(this, Observer<Resource<Boolean>> { response ->
            this@MainChatActivity.parseResource(
                response,
                object : OnResourceParseCallback<Boolean?>() {
                    override fun onSuccess(data: Boolean?) {
                        EaseThreadManager.instance.runOnIOThread(kotlinx.coroutines.Runnable {
                            DemoHelper.demoHelper.logout()
                        })

                        updateView()
                    }

                    override fun onError(code: Int, message: String?) {
                        super.onError(code, message)
                        runOnUiThread {
                            btn_login.isEnabled = true
                        }
                    }
                })
        })
    }

    override fun onResume() {
        super.onResume()
        updateView()

    }

    private fun updateView() {
        if (ChatClient.getInstance().isLoggedInBefore) {
            val username = ChatClient.getInstance().currentUser
            if (!username.isNullOrEmpty()) {
                binding.loginUserInfo.text = this.getString(R.string.login_user_info, username)
                binding.login.isEnabled = false
                binding.logout.isEnabled = true
                EaseThreadManager.instance.runOnIOThread(kotlinx.coroutines.Runnable {
                    DemoHelper.demoHelper.initEThree(
                        username,
                        mContext
                    )
                })
                return
            }
        }
        binding.loginUserInfo.text = this.getString(R.string.login_none)
        binding.login.isEnabled = true
        binding.logout.isEnabled = false
    }

    private fun initListener() {
        binding.login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            viewModel.logout(true)
        }
    }


}
