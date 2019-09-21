package org.caojun.signin.adapter

import android.view.View
import android.widget.RadioButton
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.R
import org.caojun.signin.activity.TeacherStudentListActivity
import org.caojun.signin.bmob.Teacher

class RadioButtonItem(private val teacherListener: TeacherStudentListActivity.TeacherListener): AdapterItem<Teacher> {

    private lateinit var rbName: RadioButton

    override fun getLayoutResId(): Int {
        return R.layout.item_radio_button
    }

    override fun bindViews(root: View) {
        rbName = root.findViewById(R.id.rbName)
    }

    override fun setViews() {
    }

    override fun handleData(t: Teacher, position: Int) {
        rbName.text = t.name

        if (teacherListener.getItemCheckedId() != t.objectId) {
            rbName.isChecked = false
        }

        rbName.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                teacherListener.onItemChecked(t.objectId)
            }
        }

        rbName.setOnLongClickListener {
            teacherListener.onItemLongClick(t)
            true
        }
    }
}