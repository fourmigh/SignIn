package org.caojun.signin.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_teacher_sign_list.*
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.BaseActivity
import org.caojun.signin.MainApplication
import org.caojun.signin.R
import org.caojun.signin.adapter.SignItem
import org.caojun.signin.adapter.StudentItem
import org.caojun.signin.bmob.*
import org.caojun.signin.listener.SignListener
import org.jetbrains.anko.startActivityForResult

/**
 * 教师签到列表（教师）
 * 左半边学生列表，右半边教师签到列表
 */
class TeacherSignListActivity : BaseActivity() {

    interface StudentListener {
        fun onItemChecked(student: Student)
        fun getItemCheckedId(): String
        fun onItemLongClick(student: Student)
    }

    private var student: Student? = null
    private var adapterStudent: CommonAdapter<Student>? = null
    private val students = ArrayList<Student>()
    private var adapterSign: CommonAdapter<TeacherSign>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_sign_list)

        btnAddStudent.setOnClickListener {
            doAddStudent()
        }

        btnSign.setOnClickListener {
            doSign()
        }

        refreshStudents()
    }

    override fun onResume() {
        super.onResume()
        refreshSignButton()
    }

    private fun refreshStudents() {
        students.clear()
        val teacher = MainApplication.role as Teacher
        for (s in BmobUtils.students) {
            if (s.teacherId == teacher.objectId) {
                students.add(s)
            }
        }

        if (adapterStudent == null) {
            adapterStudent = object : CommonAdapter<Student>(students, 1) {
                override fun createItem(type: Any?): AdapterItem<*> {
                    return StudentItem(object : StudentListener {
                        override fun onItemChecked(student: Student) {
                            this@TeacherSignListActivity.student = student
                            refreshSigns()
                            refreshSignButton()
                            notifyDataSetChanged()
                        }

                        override fun getItemCheckedId(): String {
                            return if (student == null) "" else student!!.objectId
                        }

                        override fun onItemLongClick(student: Student) {
                            TeacherActivity.teacher = teacher
                            startActivityForResult<TeacherActivity>(RequestCode_Teacher)
                        }
                    })
                }
            }
            lvStudent.adapter = adapterStudent
        } else {
            adapterStudent?.data = students
            adapterStudent?.notifyDataSetChanged()
        }
    }

    private fun refreshSigns() {
        if (student == null) {
            return
        }
        val teacher = MainApplication.role as Teacher

        BmobUtils.queryTeacherSigns(teacher.objectId, student!!.objectId, object : BmobUtils.Listener {
            override fun done() {
                if (adapterSign == null) {
                    adapterSign = object : CommonAdapter<TeacherSign>(BmobUtils.teacherSigns, 1) {
                        override fun createItem(type: Any?): AdapterItem<*> {
                            return SignItem(object : SignListener {
                                override fun onItemLongClick(sign: Sign) {
                                    TeacherSignActivity.sign = sign as TeacherSign
                                    doSign()
                                }
                            })
                        }
                    }
                    lvSign.adapter = adapterSign
                } else {
                    adapterSign?.data = BmobUtils.teacherSigns
                    adapterSign?.notifyDataSetChanged()
                }
            }
        })
    }

    private fun refreshSignButton() {

        btnSign.isEnabled = students.isNotEmpty()
        if (btnSign.isEnabled && student == null) {
            btnSign.isEnabled = false
        }
        if (btnSign.isEnabled && student != null) {
            var found = false
            for (t in BmobUtils.students) {
                if (t.objectId == student!!.objectId) {
                    found = true
                }
            }
            if (!found) {
                btnSign.isEnabled = false
            }
        }
    }

    private fun doAddStudent() {
        val teacher = MainApplication.role as Teacher
        val teacherId = teacher.objectId
        startActivityForResult<StudentActivity>(RequestCode_Student, StudentActivity.KEY_TEACHER_ID to teacherId)
    }

    private fun doSign() {
        if (student == null || MainApplication.geoPoint == null) {
            return
        }
        StudentSignActivity.sign?.geoPoint = MainApplication.geoPoint!!
        val distance = BmobUtils.distance(MainApplication.geoPoint!!, student!!.geoPoint)
        StudentSignActivity.sign?.distance = distance

        val studentId = student?.objectId
        val studentName = student?.name

        startActivityForResult<TeacherSignActivity>(RequestCode_Sign,
            TeacherSignActivity.KEY_STUDENT_ID to studentId,
            TeacherSignActivity.KEY_STUDENT_NAME to studentName,
            TeacherSignActivity.KEY_DISTANCE to distance)
    }
}