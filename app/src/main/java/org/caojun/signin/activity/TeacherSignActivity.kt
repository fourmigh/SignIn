package org.caojun.signin.activity

import org.caojun.signin.BaseActivity
import org.caojun.signin.bmob.TeacherSign

/**
 * 教师签到（教师）
 */
class TeacherSignActivity : BaseActivity() {

    companion object {
        const val KEY_STUDENT_ID = "KEY_STUDENT_ID"
        const val KEY_STUDENT_NAME = "KEY_STUDENT_NAME"

        var sign: TeacherSign? = null
    }
}