package org.caojun.signin.bmob

import cn.bmob.v3.datatype.BmobGeoPoint

class Student : Person() {

    //带教老师
    var masterName = ""
    //带教老师手机
    var masterMobile = ""
    //工地名称
    var signName = ""
    //签到所在地区
    var signArea = ""
    //签到详细地址
    var signAddress = ""
    //单位名称
    var companyName = ""
    //单位地址
    var companyAddress = ""

    //不可见属性
    //教师ID
    var teacherId = ""
    //签到行政区划代码
    var signAdCode = ""
    //签到区号
    var signAreaCode = ""
    //签到位置
    var geoPoint = BmobGeoPoint()
}