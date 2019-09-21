package org.caojun.signin.activity

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.bmob.Admin
import org.caojun.signin.bmob.Student
import org.caojun.signin.bmob.Teacher
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import android.text.Editable
import android.text.TextWatcher

/**
 * 登录，验证手机号，判断角色
 */
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val mobile = s.toString()
                setButton(mobile)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btnLogin.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        doAsync {
            val mobile = etMobile.text.toString()
            //TODO 手机号验证

            MainApplication.loginMobile = mobile
            uiThread {
                when {
                    MainApplication.role == null -> {
                        toast(R.string.login_idle)
                    }
                    MainApplication.role is Student -> {
                        val student = MainApplication.role as Student
                        toast(getString(R.string.login_student, student.name))
                    }
                    MainApplication.role is Teacher -> {
                        val teacher = MainApplication.role as Teacher
                        toast(getString(R.string.login_teacher, teacher.name))
                    }
                    MainApplication.role is Admin -> {
                        toast(R.string.login_admin)
                    }
                }
            }

            if (MainApplication.role != null) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setButton(etMobile.text.toString())
    }

    private fun setButton(mobile: String?) {
        btnLogin.isEnabled = !(TextUtils.isEmpty(mobile) || mobile?.length != 11)
    }
}