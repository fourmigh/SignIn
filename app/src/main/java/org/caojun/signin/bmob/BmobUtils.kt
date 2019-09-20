package org.caojun.signin.bmob

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.socks.library.KLog
import org.caojun.signin.MainApplication
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult

object BmobUtils {

    private val admins = ArrayList<Admin>()
    val teachers = ArrayList<Teacher>()
    val students = ArrayList<Student>()

    fun initialize() {
        doAsync {
            queryAdmins()
            queryTeachers()
            queryStudents()
        }
    }

    private fun queryAdmins(roles: ArrayList<Admin>) {
        val query = BmobQuery<Admin>()
        query.findObjects(object : FindListener<Admin>() {
            override fun done(list: MutableList<Admin>?, p1: BmobException?) {

                if (list?.isNotEmpty() == true) {
                    roles.clear()
                    roles.addAll(list)
                }
            }
        })
    }

    private fun queryTeachers(roles: ArrayList<Teacher>) {
        val query = BmobQuery<Teacher>()
        query.findObjects(object : FindListener<Teacher>() {
            override fun done(list: MutableList<Teacher>?, p1: BmobException?) {

                if (list?.isNotEmpty() == true) {
                    roles.clear()
                    roles.addAll(list)
                }
            }
        })
    }

    private fun queryStudents(roles: ArrayList<Student>) {
        val query = BmobQuery<Student>()
        query.findObjects(object : FindListener<Student>() {
            override fun done(list: MutableList<Student>?, p1: BmobException?) {

                if (list?.isNotEmpty() == true) {
                    roles.clear()
                    roles.addAll(list)
                }
            }
        })
    }

    fun isAdmin(mobile: String): Admin? {
        val result = doAsyncResult {
            val admins = queryAdmins()
            if (admins.isEmpty()) {
                return@doAsyncResult null
            }
            for (i in admins.indices) {
                if (admins[i].mobile == mobile) {
                    return@doAsyncResult admins[i]
                }
            }
            null
        }
        return result.get()
    }

    private fun queryAdmins(): List<Admin> {
        val result = doAsyncResult {

            if (admins.isNotEmpty()) {
                return@doAsyncResult admins
            }
            queryAdmins(admins)
            admins
        }
        return result.get()
    }

    fun isTeacher(mobile: String): Teacher? {
        val result = doAsyncResult {
            val teachers = queryTeachers()
            if (teachers.isEmpty()) {
                return@doAsyncResult null
            }
            for (i in teachers.indices) {
                if (teachers[i].mobile == mobile) {
                    return@doAsyncResult teachers[i]
                }
            }
            null
        }
        return result.get()
    }

    fun reQueryTeachers(): Boolean {
        val result = doAsyncResult {
            teachers.clear()
            queryTeachers()
            true
        }
        return result.get()
    }

    private fun queryTeachers(): List<Teacher> {
        val result = doAsyncResult {

            if (teachers.isNotEmpty()) {
                return@doAsyncResult teachers
            }
            queryTeachers(teachers)
            teachers
        }
        return result.get()
    }

    fun isStudent(mobile: String): Student? {
        val result = doAsyncResult {
            val students = queryStudents()
            if (students.isEmpty()) {
                return@doAsyncResult null
            }
            for (i in students.indices) {
                if (students[i].mobile == mobile) {
                    return@doAsyncResult students[i]
                }
            }
            null
        }
        return result.get()
    }

    fun reQueryStudents(): Boolean {
        val result = doAsyncResult {
            students.clear()
            queryStudents()
            true

        }
        return result.get()
    }

    private fun queryStudents(): List<Student> {
        val result = doAsyncResult {

            if (students.isNotEmpty()) {
                return@doAsyncResult students
            }
            queryStudents(students)
            students
        }
        return result.get()
    }
}