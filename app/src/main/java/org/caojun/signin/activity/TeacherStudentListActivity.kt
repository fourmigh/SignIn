package org.caojun.signin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_teacher_student_list.*
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.BaseActivity
import org.caojun.signin.R
import org.caojun.signin.adapter.NameItem
import org.caojun.signin.adapter.TeacherItem
import org.caojun.signin.bmob.BmobUtils
import org.caojun.signin.bmob.Student
import org.caojun.signin.bmob.Teacher
import org.jetbrains.anko.startActivityForResult

/**
 * 教师学生双列表（管理员）
 * 左半边教师列表，右半边学生列表
 */
class TeacherStudentListActivity : BaseActivity() {

    interface TeacherListener {
        fun onItemChecked(objectId: String)
        fun getItemCheckedId(): String
        fun onItemLongClick(teacher: Teacher)
    }

    interface StudentListener {
        fun onItemLongClick(student: Student)
    }

    private var teacherId = ""
    private var adapterTeacher: CommonAdapter<Teacher>? = null
    private var adapterStudent: CommonAdapter<Student>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_student_list)

        btnAddStudent.setOnClickListener {
            doAddStudent()
        }

        btnAddTeacher.setOnClickListener {
            doAddTeacher()
        }

        refreshTeachers()
    }

    override fun onResume() {
        super.onResume()
        refreshStudentButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode_Teacher -> {
                if (resultCode == Activity.RESULT_OK) {
                    BmobUtils.reQueryTeachers(object : BmobUtils.Listener {
                        override fun done() {
                            refreshTeachers()
                        }
                    })
                }
            }
            RequestCode_Student -> {
                if (resultCode == Activity.RESULT_OK) {
                    BmobUtils.reQueryStudents(object : BmobUtils.Listener {
                        override fun done() {
                            refreshStudents()
                        }
                    })
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun refreshStudentButton() {
        btnAddStudent.isEnabled = BmobUtils.teachers.isNotEmpty()
        if (btnAddStudent.isEnabled && teacherId.isEmpty()) {
            btnAddStudent.isEnabled = false
        }
        if (btnAddStudent.isEnabled && teacherId.isNotEmpty()) {
            var found = false
            for (t in BmobUtils.teachers) {
                if (t.objectId == teacherId) {
                    found = true
                }
            }
            if (!found) {
                btnAddStudent.isEnabled = false
            }
        }
    }

    private fun refreshStudents() {
        val students = ArrayList<Student>()
        for (s in BmobUtils.students) {
            if (s.teacherId == teacherId) {
                students.add(s)
            }
        }

        if (adapterStudent == null) {
            adapterStudent = object : CommonAdapter<Student>(students, 1) {
                override fun createItem(type: Any?): AdapterItem<*> {
                    return NameItem(object : StudentListener {
                        override fun onItemLongClick(student: Student) {
                            StudentActivity.student = student
                            startActivityForResult<StudentActivity>(RequestCode_Student)
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

    private fun refreshTeachers() {
        if (adapterTeacher == null) {
            adapterTeacher = object : CommonAdapter<Teacher>(BmobUtils.teachers, 1) {
                override fun createItem(type: Any?): AdapterItem<*> {
                    return TeacherItem(object : TeacherListener {
                        override fun onItemChecked(objectId: String) {
                            this@TeacherStudentListActivity.teacherId = objectId
                            refreshStudents()
                            refreshStudentButton()
                            notifyDataSetChanged()
                        }

                        override fun getItemCheckedId(): String {
                            return teacherId
                        }

                        override fun onItemLongClick(teacher: Teacher) {
                            TeacherActivity.teacher = teacher
                            startActivityForResult<TeacherActivity>(RequestCode_Teacher)
                        }
                    })
                }
            }
            lvTeacher.adapter = adapterTeacher
        } else {
            adapterTeacher?.data = BmobUtils.teachers
            adapterTeacher?.notifyDataSetChanged()
        }
    }

    private fun doAddTeacher() {
        startActivityForResult<TeacherActivity>(RequestCode_Teacher)
    }

    private fun doAddStudent() {
        startActivityForResult<StudentActivity>(RequestCode_Student, StudentActivity.KEY_TEACHER_ID to teacherId)
    }
}