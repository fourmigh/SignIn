package org.caojun.signin.adapter

import android.view.View
import android.widget.TextView
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.R
import org.caojun.signin.activity.TeacherStudentListActivity
import org.caojun.signin.bmob.Student
import org.caojun.signin.utils.GDMapUtils

class NameItem(private val listener: TeacherStudentListActivity.StudentListener? = null): AdapterItem<Student> {

    private lateinit var tvName: TextView

    override fun getLayoutResId(): Int {
        return R.layout.item_name
    }

    override fun bindViews(root: View) {
        tvName = root.findViewById(R.id.tvName)
    }

    override fun setViews() {
    }

    override fun handleData(t: Student, position: Int) {
        tvName.text = t.name

        tvName.setOnClickListener {
            GDMapUtils.moveMap(t)
        }

        tvName.setOnLongClickListener {
            listener?.onItemLongClick(t)
            true
        }
    }
}