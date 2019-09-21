package org.caojun.signin.adapter

import android.view.View
import android.widget.TextView
import org.caojun.adapter.bean.AdapterItem
import org.caojun.signin.R
import org.caojun.signin.bmob.Sign
import org.caojun.signin.bmob.StudentSign
import org.caojun.signin.bmob.TeacherSign
import org.caojun.signin.listener.SignListener

class SignItem(private val listener: SignListener): AdapterItem<Sign> {

    private lateinit var tvDate: TextView

    override fun getLayoutResId(): Int {
        return R.layout.item_sign
    }

    override fun bindViews(root: View) {
        tvDate = root.findViewById(R.id.tvDate)
    }

    override fun setViews() {
    }

    override fun handleData(t: Sign, position: Int) {
        tvDate.text = t.createdAt

        tvDate.setOnLongClickListener {
            when (t) {
                is StudentSign -> {
                    listener.onItemLongClick(t)
                }
                is TeacherSign -> {
                    listener.onItemLongClick(t)
                }
            }
            true
        }
    }
}