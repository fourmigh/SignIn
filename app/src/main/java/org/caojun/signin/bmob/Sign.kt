package org.caojun.signin.bmob

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobGeoPoint

open class Sign : BmobObject() {

    var isDeleted = false
    var studentId = ""
    var studentName = ""
    var geoPoint = BmobGeoPoint()
    //签到距离
    var distance = 0F
}