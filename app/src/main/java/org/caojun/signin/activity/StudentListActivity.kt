package org.caojun.signin.activity

import android.os.Bundle
import org.caojun.signin.BaseActivity
import org.caojun.signin.R

/**
 * 学生列表（教师）
 */
class StudentListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)
    }
}