package org.caojun.signin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import kotlinx.android.synthetic.main.activity_teacher_sign.*
import org.caojun.multiimageselector.MultiImageSelector
import org.caojun.multiimageselector.moments.activity.MultiImageSelectorActivity
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.bmob.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * 教师签到（教师）
 */
class TeacherSignActivity : BaseActivity() {

    companion object {
        const val KEY_STUDENT_ID = "KEY_STUDENT_ID"
        const val KEY_STUDENT_NAME = "KEY_STUDENT_NAME"
        const val KEY_DISTANCE = "KEY_DISTANCE"

        var sign: TeacherSign? = null
    }

    private var studentId = ""
    private var studentName = ""
    private var distance = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_sign)

        studentId = intent.getStringExtra(KEY_STUDENT_ID)
        studentName = intent.getStringExtra(KEY_STUDENT_NAME)
        distance = intent.getFloatExtra(KEY_DISTANCE, 0F)

        if (sign != null) {
            etTime.setText(sign?.createdAt)
            etDistance.setText(getString(R.string.distance_value, sign?.distance))
            etGuidance.setText(sign?.guidance)
            etPerformance.setText(sign?.performance)
            btnDelete.isEnabled = true
        } else {
            tilTime.visibility = View.GONE
            etGuidance.setText("")
            etPerformance.setText("")
            etDistance.setText(getString(R.string.distance_value, distance))

            btnDelete.isEnabled = false
        }

        etGuidance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnSave.isEnabled = !s.isNullOrEmpty()
                if (btnSave.isEnabled && etPerformance.text.isNullOrEmpty()) {
                    btnSave.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        etPerformance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnSave.isEnabled = !s.isNullOrEmpty()
                if (btnSave.isEnabled && etGuidance.text.isNullOrEmpty()) {
                    btnSave.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        ibPicture.setOnClickListener {

            MultiImageSelector.create()
                .showCamera(true)
                .single()
                .start(this, RequestCode_Picture)
        }

        btnDelete.isEnabled = MainApplication.role is Admin
        btnSave.isEnabled = false

        btnDelete.setOnClickListener {
            doDelete()
        }

        btnSave.setOnClickListener {
            doSave()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode_Picture && resultCode == Activity.RESULT_OK) {//从相册选择完图片
            if (data != null) {
                val images = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT)
//                Glide.with(this@GoodsDetailActivity).load(images[0]).into(ivPicture)
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        sign = null
        super.finish()
    }

    private fun doDelete() {
        sign?.isDeleted = true
        doSaveSign(true)
    }

    private fun doSave() {
        var isUpdate = true
        if (sign == null) {
            isUpdate = false
            sign = TeacherSign()

            val teacher = MainApplication.role as Teacher

            sign?.studentId = studentId
            sign?.studentName = studentName
            sign?.teacherId = teacher.objectId
            sign?.teacherName = teacher.name

            if (MainApplication.geoPoint != null) {
                sign?.geoPoint = MainApplication.geoPoint!!
                sign?.distance = distance
            }
        }
        sign?.guidance = etGuidance.text.toString()
        sign?.performance = etPerformance.text.toString()

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