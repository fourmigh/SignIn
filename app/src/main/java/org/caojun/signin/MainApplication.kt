package org.caojun.signin

import android.app.Application
import android.content.Context
import android.text.TextUtils
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobGeoPoint
import com.socks.library.KLog
import org.caojun.signin.BuildConfig.DEBUG
import org.caojun.signin.bmob.BmobUtils
import org.caojun.utils.ManifestUtils

/**
 * Created by CaoJun on 2019/9/8.
 */
class MainApplication : Application() {

    companion object {
        var loginMobile = ""
        var role: BmobObject? = null
            get() {
                if (field == null) {
                    val admin = BmobUtils.isAdmin(loginMobile)
                    if (admin != null) {
                        field = admin
                        return field
                    }
                    val teacher = BmobUtils.isTeacher(loginMobile)
                    if (teacher != null) {
                        field = teacher
                        return field
                    }
                    val student = BmobUtils.isStudent(loginMobile)
                    if (student != null) {
                        field = student
                        return field
                    }
                }
                return field
            }

        fun logout() {
            loginMobile = ""
            role = null
        }

        var province = ""
        var geoPoint: BmobGeoPoint? = null
    }

    override fun onCreate() {
        super.onCreate()
        KLog.init(DEBUG)
        Bmob.initialize(this, ManifestUtils.getApplicationMetaData(this, "cn.bmob.v3.appid"))
        BmobUtils.initialize()
    }
}