package org.caojun.signin.bmob

import cn.bmob.v3.BmobObject

open class Person : BmobObject() {

    var name = ""
    var mobile = ""
    var isDeleted = false
}