package org.caojun.signin.adapter

import android.view.View
import android.widget.RadioButton
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.R
import org.caojun.signin.activity.TeacherSignListActivity
import org.caojun.signin.bmob.Student

class StudentItem(private val listener: TeacherSignListActivity.StudentListener): AdapterItem<Student> {

    private lateinit var rbName: RadioButton

    override fun getLayoutResId(): Int {
        return R.layout.item_radio_button
    }

    override fun bindViews(root: View) {
        rbName = root.findViewById(R.id.rbName)
    }

    override fun setViews() {
    }

    override fun handleData(t: Student, position: Int) {
        rbName.text = t.name

        if (listener.getItemCheckedId() != t.objectId) {
            rbName.isChecked = false
        }

        rbName.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                listener.onItemChecked(t)
            }
        }

        rbName.setOnLongClickListener {
            listener.onItemLongClick(t)
            true
        }
    }
}