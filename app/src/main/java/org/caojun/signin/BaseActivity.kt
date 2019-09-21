package org.caojun.signin

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import org.caojun.signin.activity.LoginActivity
import org.caojun.signin.bmob.BmobUtils
import org.caojun.utils.ActivityUtils
import org.jetbrains.anko.startActivityForResult

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val ResultCode_Exit = 47
        const val RequestCode_Login = 1
        const val RequestCode_Teacher = 2
        const val RequestCode_Student = 3
    }

    override fun onResume() {
        super.onResume()
        if (this !is LoginActivity && (TextUtils.isEmpty(MainApplication.loginMobile) || MainApplication.role == null)) {
            startActivityForResult<LoginActivity>(RequestCode_Login)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode_Login -> {
                if (resultCode != Activity.RESULT_OK) {
                    setResult(ResultCode_Exit)
                    finish()
                    return
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    protected fun checkSelfPermission(permission: String, listener: ActivityUtils.RequestPermissionListener): Boolean {
        return ActivityUtils.checkSelfPermission(this, permission, listener)
    }

    protected fun call(number: String) {
        ActivityUtils.call(this, number)
    }
}