package org.caojun.signin.bmob

import cn.bmob.v3.datatype.BmobFile

class TeacherSign : Sign() {

    var teacherId = ""
    var teacherName = ""
    var picture = BmobFile()
    //实习表现
    var performance = ""
    //实习指导
    var guidance = ""
}