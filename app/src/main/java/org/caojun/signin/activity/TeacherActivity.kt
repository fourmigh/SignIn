package org.caojun.signin.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import kotlinx.android.synthetic.main.activity_teacher.*
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.bmob.Admin
import org.caojun.signin.bmob.Teacher
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * 教师表信息编辑（管理员、教师）
 */
class TeacherActivity : BaseActivity() {

    companion object {
        var teacher: Teacher? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        if (teacher != null) {
            etMobile.setText(teacher?.mobile)
            etName.setText(teacher?.name)
            btnDelete.isEnabled = true
        } else {
            etMobile.setText("")
            etName.setText("")
            btnDelete.isEnabled = false
        }

        btnDelete.isEnabled = MainApplication.role is Admin
        btnSave.isEnabled = false

        etMobile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty() || s.length != 11) {
                    btnSave.isEnabled = false
                } else {
                    btnSave.isEnabled = !etName.text.isNullOrEmpty()
                    checkChange()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    btnSave.isEnabled = false
                } else {
                    btnSave.isEnabled = !(etMobile.text.isNullOrEmpty() || etMobile.text?.length != 11)
                    checkChange()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btnDelete.setOnClickListener {
            doDelete()
        }

        btnSave.setOnClickListener {
            doSave()
        }
    }

    private fun doSave() {
        var isUpdate = true
        if (teacher == null) {
            isUpdate = false
            teacher = Teacher()
        }
        teacher?.mobile = etMobile.text.toString()
        teacher?.name = etName.text.toString()
        doSaveTeacher(isUpdate)
    }

    private fun doSaveTeacher(isUpdate: Boolean) {
        doAsync {
            if (isUpdate) {
                teacher?.update(object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        doDone(e)
                    }
                })
            } else {
                teacher?.save(object : SaveListener<String>() {
                    override fun done(o: String, e: BmobException?) {
                        doDone(e)
                    }
                })
            }
        }
    }

    private fun doDone(e: BmobException?) {
        if (e == null) {
            teacher = null
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            runOnUiThread {
                toast(getString(R.string.bmob_error, e))
            }
        }
    }

    private fun doDelete() {
        if (teacher == null) {
            return
        }
        teacher?.isDeleted = true
        doSaveTeacher(true)
    }

    private fun checkChange() {
        if (teacher != null && btnSave.isEnabled) {
            if (teacher?.name == etName.text.toString() && teacher?.mobile == etMobile.text.toString()) {
                btnSave.isEnabled = false
            }
        }
    }
}