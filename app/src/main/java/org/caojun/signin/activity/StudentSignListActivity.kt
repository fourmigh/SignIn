package org.caojun.signin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_list.*
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.adapter.SignItem
import org.caojun.signin.bmob.BmobUtils
import org.caojun.signin.bmob.Sign
import org.caojun.signin.bmob.Student
import org.caojun.signin.bmob.StudentSign
import org.caojun.signin.listener.SignListener
import org.jetbrains.anko.startActivityForResult

/**
 * 学生签到列表（学生）
 */
class StudentSignListActivity : BaseActivity() {

    private var adapter: CommonAdapter<StudentSign>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_list)

        btnSign.setOnClickListener {
            doSign()
        }

        refreshData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode_Sign -> {
                if (resultCode == Activity.RESULT_OK) {
                    refreshData()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun doSign() {
        startActivityForResult<StudentSignActivity>(RequestCode_Sign)
    }

    private fun refreshData() {
        val student = MainApplication.role as Student
        BmobUtils.queryStudentSigns(student.objectId, object : BmobUtils.Listener {
            override fun done() {
                if (adapter == null) {
                    adapter = object : CommonAdapter<StudentSign>(BmobUtils.studentSigns, 1) {
                        override fun createItem(type: Any?): AdapterItem<*> {
                            return SignItem(object : SignListener {
                                override fun onItemLongClick(sign: Sign) {
                                    StudentSignActivity.sign = sign as StudentSign
                                    doSign()
                                }
                            })
                        }
                    }
                    lvSign.adapter = adapter
                } else {
                    adapter?.data = BmobUtils.studentSigns
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }
}