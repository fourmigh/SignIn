package org.caojun.signin.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import kotlinx.android.synthetic.main.activity_student_sign.*
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.bmob.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * 学生签到编辑（学生）
 */
class StudentSignActivity : BaseActivity() {

    companion object {
        var sign: StudentSign? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_sign)

        if (sign != null) {
            etTime.setText(sign?.createdAt)
            etDistance.setText(getString(R.string.distance_value, sign?.distance))
            etTask.setText(sign?.task)
            btnDelete.isEnabled = true
            btnSave.isEnabled = false
        } else {
            tilTime.visibility = View.GONE
            val student = MainApplication.role as Student
            val distance = if (MainApplication.geoPoint == null) 0F else BmobUtils.distance(MainApplication.geoPoint!!, student.geoPoint)
            etDistance.setText(getString(R.string.distance_value, distance))
            etTask.setText(R.string.sign_task_value)
            btnDelete.isEnabled = false
            btnSave.isEnabled = true
        }

        etTask.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnSave.isEnabled = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btnDelete.isEnabled = MainApplication.role is Admin

        btnDelete.setOnClickListener {
            doDelete()
        }

        btnSave.setOnClickListener {
            doSave()
        }
    }

    private fun doDelete() {
        sign?.isDeleted = true
        doSaveSign(true)
    }

    private fun doSave() {
        var isUpdate = true
        if (sign == null) {
            isUpdate = false
            sign = StudentSign()

            val student = MainApplication.role as Student

            sign?.studentId = student.objectId
            sign?.studentName = student.name
            if (MainApplication.geoPoint != null) {
                sign?.geoPoint = MainApplication.geoPoint!!
                val distance = BmobUtils.distance(MainApplication.geoPoint!!, student.geoPoint)
                sign?.distance = distance
            }
        }
        sign?.task = etTask.text.toString()

        doSaveSign(isUpdate)
    }

    private fun doSaveSign(isUpdate: Boolean) {
        doAsync {

            if (isUpdate) {
                sign?.update(object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        doDone(e)
                    }
                })
            } else {
                sign?.save(object : SaveListener<String>() {
                    override fun done(o: String, e: BmobException?) {
                        doDone(e)
                    }
                })
            }
        }
    }

    override fun finish() {
        sign = null
        super.finish()
    }

    private fun doDone(e: BmobException?) {
        if (e == null) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            runOnUiThread {
                toast(getString(R.string.bmob_error, e))
            }
        }
    }
}