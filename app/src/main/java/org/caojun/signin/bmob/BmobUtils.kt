package org.caojun.signin.bmob

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobGeoPoint
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.amap.api.maps.model.LatLng
import com.socks.library.KLog
import org.caojun.signin.MainApplication
import org.caojun.signin.utils.GDMapUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread

object BmobUtils {

    private val admins = ArrayList<Admin>()
    val teachers = ArrayList<Teacher>()
    val students = ArrayList<Student>()
    val studentSigns = ArrayList<StudentSign>()
    val teacherSigns = ArrayList<TeacherSign>()

    interface Listener {
        fun done()
    }

    fun initialize() {
        doAsync {
            queryAdmins()
            queryTeachers()
            queryStudents()
        }
    }

    private fun reQueryAdmins(listener: Listener) {
        val query = BmobQuery<Admin>()
        query.findObjects(object : FindListener<Admin>() {
            override fun done(list: MutableList<Admin>?, e: BmobException?) {

                doAsync {
                    if (list?.isNotEmpty() == true) {
                        admins.clear()
                        admins.addAll(list)
                    }
                    uiThread {
                        listener.done()
                    }
                }
            }
        })
    }

    fun isAdmin(mobile: String): Admin? {
        if (admins.isEmpty()) {
            return null
        }
        for (i in admins.indices) {
            if (admins[i].mobile == mobile) {
                return admins[i]
            }
        }
        return null
    }

    private fun queryAdmins() {
        if (admins.isNotEmpty()) {
            return
        }
        reQueryAdmins(object : Listener {
            override fun done() {
                return
            }
        })
    }

    fun reQueryTeachers(listener: Listener) {
        val query = BmobQuery<Teacher>()
        query.addWhereEqualTo("isDeleted", false)
        query.findObjects(object : FindListener<Teacher>() {
            override fun done(list: MutableList<Teacher>?, e: BmobException?) {

                doAsync {
                    teachers.clear()
                    if (list?.isNotEmpty() == true) {
                        teachers.addAll(list)
                    }
                    uiThread {
                        listener.done()
                    }
                }
            }
        })
    }

    fun isTeacher(mobile: String): Teacher? {
        if (teachers.isEmpty()) {
            return null
        }
        for (i in teachers.indices) {
            if (teachers[i].mobile == mobile) {
                return teachers[i]
            }
        }
        return null
    }

    private fun queryTeachers() {
        if (teachers.isNotEmpty()) {
            return
        }
        reQueryTeachers(object : Listener {
            override fun done() {
                return
            }
        })
    }

    fun reQueryStudents(listener: Listener) {
        val query = BmobQuery<Student>()
        query.addWhereEqualTo("isDeleted", false)
        query.findObjects(object : FindListener<Student>() {
            override fun done(list: MutableList<Student>?, e: BmobException?) {

                doAsync {
                    students.clear()
                    if (list?.isNotEmpty() == true) {
                        students.addAll(list)
                    }
                    uiThread {
                        listener.done()
                    }
                }
            }
        })
    }

    fun isStudent(mobile: String): Student? {
        if (students.isEmpty()) {
            return null
        }
        for (i in students.indices) {
            if (students[i].mobile == mobile) {
                return students[i]
            }
        }
        return null
    }

    private fun queryStudents() {
        if (students.isNotEmpty()) {
            return
        }
        reQueryStudents(object : Listener {
            override fun done() {
                return
            }
        })
    }

    fun queryStudentSigns(studentId: String, listener: Listener) {
        val query = BmobQuery<StudentSign>()
        query.addWhereEqualTo("isDeleted", false)
        query.addWhereEqualTo("studentId", studentId)
        query.findObjects(object : FindListener<StudentSign>() {
            override fun done(list: MutableList<StudentSign>?, e: BmobException?) {

                doAsync {
                    studentSigns.clear()
                    if (list?.isNotEmpty() == true) {
                        studentSigns.addAll(list)
                    }
                    uiThread {
                        listener.done()
                    }
                }
            }
        })
    }

    fun queryTeacherSigns(teacherId: String, studentId: String, listener: Listener) {
        val query = BmobQuery<TeacherSign>()
        query.addWhereEqualTo("isDeleted", false)
        query.addWhereEqualTo("teacherId", teacherId)
        query.addWhereEqualTo("studentId", studentId)
        query.findObjects(object : FindListener<TeacherSign>() {
            override fun done(list: MutableList<TeacherSign>?, e: BmobException?) {

                doAsync {
                    teacherSigns.clear()
                    if (list?.isNotEmpty() == true) {
                        teacherSigns.addAll(list)
                    }
                    uiThread {
                        listener.done()
                    }
                }
            }
        })
    }

    fun distance(start: BmobGeoPoint, end: BmobGeoPoint): Float {
        val startLatLng = LatLng(start.latitude, start.longitude)
        val endLatLng = LatLng(end.latitude, end.longitude)
        return GDMapUtils.distance(startLatLng, endLatLng)
    }
}