package org.caojun.signin.bmob

import cn.bmob.v3.datatype.BmobDate
import cn.bmob.v3.datatype.BmobFile
import java.util.*

class TeacherSign : Sign() {

    var date = BmobDate(Date())
    var picture = BmobFile()
    //实习表现
    var performance = ""
    //实习指导
    var guidance = ""
}